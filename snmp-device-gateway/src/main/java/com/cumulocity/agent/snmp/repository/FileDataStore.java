package com.cumulocity.agent.snmp.repository;

import static java.util.Optional.empty;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.DisposableBean;

import com.cumulocity.agent.snmp.bootstrap.model.DeviceCredentials;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;
/**
 * The actual persistent implementation is not merged yet.
 * TODO : This implementation is subjected to change.
 */
@Slf4j
@RequiredArgsConstructor
public class FileDataStore implements DataStore, DisposableBean {

	private static String FILE_PATH = System.getProperty("user.home") + File.separator + ".snmp" + File.separator
			+ "device-credentials";

	@Override
	public void store(Serializable value) {
		remove();

		try {
			FileOutputStream fileOut = new FileOutputStream(FILE_PATH);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(value);
			objectOut.close();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<DeviceCredentials> get() {
		try {
			FileInputStream fileIn = new FileInputStream(FILE_PATH);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			DeviceCredentials credentials = (DeviceCredentials) objectIn.readObject();
			objectIn.close();

			return Optional.ofNullable(credentials);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return empty();
		}

	}

	@Override
	public void remove() {
		try {
			File file = new File(FILE_PATH);
			file.delete();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	@Override
	public void destroy() throws Exception {
	}
}