"use strict";

/********************* Slack *********************/

// Create a new instance of the WebClient class with the OAuth Access Token
const { WebClient } = require("@slack/web-api");
const web = new WebClient("xoxp-YOUR-TOKEN-GOES-HERE");

// Set your channel ID to know where to send messages to
const channelId = "MJGBXXX";

// Format a message and post it to the channel
async function postSlackMessage (adata) {
    // Alarm severity
    let color = {
        "WARNING" : "#1c8ce3",
        "MINOR"   : "#ff801f",
        "MAJOR"   : "#e66400",
        "CRITICAL": "#e0000e"
    };

    // Send a message from this app to the specified channel
    let src = adata.source;
    await web.chat.postMessage({
        channel: channelId,
        attachments : [{
            "text": adata.text,
            "fields": [
                {
                    "title": "Source",
                    "value": `<${src.self}|${src.name ? src.name : src.id}>`,
                    "short": true
                },
                {
                    "title": "Alarm type",
                    "value": adata.type,
                    "short": true
                }
            ],
            "color": color[adata.severity]
        }]
    });
}


/********************* Cumulocity *********************/

const { Client } = require ("@c8y/client");
const { BasicAuth } = require ("@c8y/client");

// Platform credentials
const auth = new BasicAuth({
    user:     "<user>",
    password: "<password>",
    tenant:   "<tenant>"
});

(async () => {
    try {
        // Platform authentication
        const client = await new Client(auth, process.env.C8Y_BASEURL);

        // List filter for unresolved alarms only
        const filter = {
            pageSize: 100,
            withTotalPages: true,
            resolved: false
        };

        // Get filtered alarms and post a message to Slack
        const { data } = await client.alarm.list(filter);
        data.forEach(alarm => {
            postSlackMessage(alarm);
        });

        // Real time subscription for active alarms
        client.realtime.subscribe("/alarms/*", (alarm) => {
            if (alarm.data.data.status === "ACTIVE") {
                postSlackMessage(alarm.data.data);
            }
        });
        console.log("listening to alarms...");
    }
    catch (err) {
        console.error(err);
    }
})();
