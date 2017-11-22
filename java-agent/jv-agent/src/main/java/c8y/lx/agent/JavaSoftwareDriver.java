/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package c8y.lx.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;

import c8y.SoftwareList;
import c8y.SoftwareListEntry;
import com.cumulocity.sdk.client.inventory.BinariesApi;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;

/**
 * A software manager that permits remote update of the agent software and
 * drivers from the platform. Works with the jar files installed in the "lib"
 * directory. After a successful update, the driver terminates the agent process
 * and finishes the update at next startup of the agent (through a wrapper script).
 */
public class JavaSoftwareDriver implements Driver, OperationExecutor {

	private static final String DOWNLOADING = ".download";
	private static final String SOFTWARE_PATH = "./lib/";
	private static Logger logger = LoggerFactory.getLogger(JavaSoftwareDriver.class);

	private SoftwareList software = new SoftwareList();
	private HashMap<String, SoftwareListEntry> sftMap = new HashMap<String, SoftwareListEntry>();

	private BinariesApi binaries;

	private GId deviceId;
	
    @Override
    public void initialize() throws Exception {
        new File(SOFTWARE_PATH).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String file) {
                if (file.matches(".*[.]jar$")) {
                	file=file.substring(0,file.length()-4  	);
                	SoftwareListEntry s = new SoftwareListEntry();
                    s.setName(file.split("-[0-9]+[.]")[0]);
                    s.setVersion(file.split(s.getName()+"-")[1]);
                    s.setUrl(" ");
                    if(!sftMap.containsKey(s.getName())) {
						software.add(s);
						sftMap.put(s.getName(), s);
					}
                }
                return false;
            }
        });
    }


	@Override
	public void initialize(Platform platform) throws Exception {
	    // Nothing to do here.
        binaries = platform.getBinariesApi();
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[] { this };
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		mo.set(software);
		OpsUtil.addSupportedOperation(mo, supportedOperationType());
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation mo) {
		this.deviceId = mo.getId();
	}

	@Override
	public void start() {
		// Nothing to do here.
	}

	@Override
	public String supportedOperationType() {
		return "c8y_SoftwareList";
	}

	@Override
	public void execute(OperationRepresentation operation, boolean cleanup) throws Exception {
		if (!deviceId.equals(operation.getDeviceId())) {
			// Silently ignore the operation if it is not targeted to us,
			// another driver will (hopefully) care.
			return;
		}

		if (cleanup) {
			finishExecuting(operation);
		} else {
			executePending(operation);
		}
	}

	private void finishExecuting(OperationRepresentation operation) {
		logger.info("Checking installed software base after update");
		SoftwareList shouldBeInstalled = operation.get(SoftwareList.class);

        HashMap<String, SoftwareListEntry> shouldBeInstalledMap = new HashMap<String, SoftwareListEntry>();

		OperationStatus status = OperationStatus.SUCCESSFUL;
		String notInstalled = null;
        String notDeleted = null;

		for (SoftwareListEntry shouldBeInstalledEntry : shouldBeInstalled) {
            shouldBeInstalledMap.put(shouldBeInstalledEntry.getName(),shouldBeInstalledEntry);
		    SoftwareListEntry installedEntry = sftMap.get(shouldBeInstalledEntry.getName());
            if (installedEntry==null || !shouldBeInstalledEntry.getVersion().equals(installedEntry.getVersion())) {
                if (notInstalled==null)
                    notInstalled="";
                status = OperationStatus.FAILED;
                notInstalled += " " + shouldBeInstalledEntry.getName() + '-' + shouldBeInstalledEntry.getVersion() + ".jar";
            }
        }

        for (SoftwareListEntry installedSoftwareEntry : software){
		    if (shouldBeInstalledMap.get(installedSoftwareEntry.getName())==null) {
                if (notDeleted == null)
                    notDeleted = "";
                status = OperationStatus.FAILED;
                notDeleted += " " + installedSoftwareEntry.getName() + '-' + installedSoftwareEntry.getVersion() + ".jar";
            }
        }

		operation.setStatus(status.toString());
		if(status!=OperationStatus.SUCCESSFUL)
		    operation.setFailureReason( (notDeleted!=null?"Not deleted:"+notDeleted:"") +
                                        (notInstalled!=null?"Not installed:"+notInstalled:"") );
	}

	private void executePending(OperationRepresentation operation)
			throws IOException {
        SoftwareList softwareUpdateOperation = (SoftwareList) operation.get(SoftwareList.class)
                .clone();
        SoftwareList toBeRemoved = new SoftwareList();
        SoftwareList toBeDownloaded = new SoftwareList();

        HashMap<String, SoftwareListEntry> sftUpdOpMap = new HashMap<String, SoftwareListEntry>();

        for (SoftwareListEntry newSoft : softwareUpdateOperation) {
            sftUpdOpMap.put(newSoft.getName(), newSoft);
            SoftwareListEntry installedSoft = sftMap.get(newSoft.getName());

            if (newSoft.getUrl().matches("^https?:\\/\\/.+\\/inventory\\/binaries\\/[0-9]+$")) {
                if (installedSoft == null)
                    toBeDownloaded.add(newSoft);
                else if (!installedSoft.getVersion().equals(newSoft.getVersion())) {
                    toBeDownloaded.add(newSoft);
                    toBeRemoved.add(installedSoft);
                } else
                    logger.warn("Package {} already installed. Skipping...", newSoft.getName() + "-" + newSoft.getVersion());
            } else {
                if (installedSoft == null || !installedSoft.getVersion().equals(newSoft.getVersion()))
                    logger.warn("Unknown software url for package {}-{}:{}", newSoft.getName(), newSoft.getVersion(), newSoft.getUrl());
            }
        }

        for (SoftwareListEntry entr : software) {
            SoftwareListEntry opEntry = sftUpdOpMap.get(entr.getName());
            if (opEntry == null)
                toBeRemoved.add(entr);
        }

        if (!toBeDownloaded.isEmpty() && !toBeRemoved.isEmpty()) {
            download(toBeDownloaded, toBeRemoved);
            rename(toBeDownloaded);
            remove(toBeRemoved);
            System.exit(0); // Delete files and restart process through watcher
        } else {
            operation.setStatus(OperationStatus.FAILED.toString());
            operation.setFailureReason("Nothing to install/upgrade.");
        }

	}

	private void download(SoftwareList toBeDownloaded, SoftwareList toBeRemoved) {
    	for (SoftwareListEntry toBeDownloadedEntry : toBeDownloaded) {
			logger.debug("Downloading " + toBeDownloadedEntry);

			String fileName= toBeDownloadedEntry.getName()+"-"+toBeDownloadedEntry.getVersion()+".jar";
			InputStream is = binaries.downloadFile(new GId(toBeDownloadedEntry.getUrl().split("inventory\\/binaries\\/")[1]));
			String filePath = SOFTWARE_PATH + fileName + DOWNLOADING;
			File targetFile = new File(SOFTWARE_PATH + fileName + DOWNLOADING);
			FileOutputStream outputStream = null;
			try {
				outputStream  = new FileOutputStream(targetFile);
				byte[] buffer = new byte[8 * 1024];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			} catch (Exception e) {
				logger.warn("Error downloading {}:{}",toBeDownloadedEntry.getName()+"-"+toBeDownloadedEntry.getVersion(), toBeDownloadedEntry.getUrl(),e);
				toBeRemoved.remove(toBeDownloadedEntry);
				toBeDownloaded.remove(toBeDownloadedEntry);
				targetFile.deleteOnExit();
			} finally {
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(outputStream);
			}
		}
	}

	private void rename(SoftwareList toBeRenamed) throws MalformedURLException{
		for (SoftwareListEntry pkg : toBeRenamed) {
			String fileName = pkg.getName()+"-"+pkg.getVersion()+".jar";
			File downloaded = new File(SOFTWARE_PATH + fileName + DOWNLOADING);
			File target = new File(SOFTWARE_PATH + fileName);
			if (!downloaded.renameTo(target))
			    logger.warn("Renaming failed {}:{}",pkg.getName()+"-"+pkg.getVersion(), pkg.getUrl());
		}
	}

	private void remove(SoftwareList toBeRemoved) {
		for (SoftwareListEntry pkg : toBeRemoved) {
		    String fileName = pkg.getName()+"-"+pkg.getVersion()+".jar";
			logger.debug("Removing " + fileName);
			new File(SOFTWARE_PATH + fileName).deleteOnExit();
		}
	}
}
