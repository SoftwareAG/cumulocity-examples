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

package c8y.trackeragent;

import java.util.Date;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.event.EventApi;

public class TracelogDriver extends AppenderBase<ILoggingEvent> {
	public static final String LOG_TYPE = "c8y_DeviceLog";

	public static final String ALARM_LEVEL_PROP = "c8y.log.alarmLevel";
	public static final String DEFAULT_ALARM_LEVEL = "ERROR";

	public static final String EVENT_LEVEL_PROP = "c8y.log.eventLevel";
	public static final String DEFAULT_EVENT_LEVEL = "INFO";

	public TracelogDriver(Platform platform, ManagedObjectRepresentation mo) {
		alarms = platform.getAlarmApi();
		events = platform.getEventApi();

		ManagedObjectRepresentation source = new ManagedObjectRepresentation();
		source.setId(mo.getId());
		source.setSelf(mo.getSelf());
		
		alarmTemplate.setType(LOG_TYPE);
		alarmTemplate.setSource(source);
		alarmTemplate.setStatus(CumulocityAlarmStatuses.ACTIVE.toString());
		alarmTemplate.setSeverity(CumulocitySeverities.MAJOR.toString());

		eventTemplate.setType(LOG_TYPE);
		eventTemplate.setSource(source);
		
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		setContext(lc);
		lc.getLogger("root").addAppender(this);
		start(); 
	}

	@Override
	protected void append(ILoggingEvent entry) {
		try {
			if (entry.getLevel().isGreaterOrEqual(alarmLevel)) {
				StringBuffer msg = new StringBuffer(entry.getLoggerName());
				msg.append(": ");
				msg.append(entry.getMessage());
				if (entry.getThrowableProxy() != null) {
					msg.append(" - ");
					msg.append(entry.getThrowableProxy().getMessage());
				}
				alarmTemplate.setTime(new Date());
				alarmTemplate.setText(msg.toString());
				alarms.create(alarmTemplate);
			} else if (entry.getLevel().isGreaterOrEqual(eventLevel)) {
				eventTemplate.setTime(new Date());
				eventTemplate.setText(entry.getLoggerName() + ": "
						+ entry.getMessage());
				events.create(eventTemplate);
			}
		} catch (SDKException e) {
			// Tough luck.
			e.printStackTrace();
		}
	}

	private AlarmApi alarms;
	private AlarmRepresentation alarmTemplate = new AlarmRepresentation();
	private Level alarmLevel = Level.toLevel(DEFAULT_ALARM_LEVEL);

	private EventApi events;
	private EventRepresentation eventTemplate = new EventRepresentation();
	private Level eventLevel = Level.toLevel(DEFAULT_EVENT_LEVEL);
}
