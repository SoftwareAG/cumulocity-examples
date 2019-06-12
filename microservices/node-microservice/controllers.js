"use strict";

/********************* Slack *********************/

// Create a new instance of the WebClient class with the OAuth Access Token
const { WebClient } = require("@slack/web-api");
const web = new WebClient(process.env.SLACK_OAUTH_TOKEN);

// Slack channel ID to know where to send messages to
const channelId = process.env.SLACK_CHANNEL_ID;

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

const Promise = require("bluebird");
const request = Promise.promisify(require("request"));

const { Client } = require ("@c8y/client");
const { BasicAuth } = require ("@c8y/client");

const baseUrl = process.env.C8Y_BASEURL;
const serviceAuth = {
    user: `${process.env.C8Y_BOOTSTRAP_TENANT}/${process.env.C8Y_BOOTSTRAP_USER}`,
    pass: process.env.C8Y_BOOTSTRAP_PASSWORD,
    sendImmediately: true
};

let cachedUsers = [];

// Get the subscribed users
function getUsers () {
    return request({
        baseUrl,
        url: `/application/currentApplication/subscriptions`,
        json: true,
        auth: serviceAuth
    })
    .then((res) => res.body.users)
    .tap((users) => {
        cachedUsers = users;
    });
}

// where the magic happens...
(async () => {

    await getUsers();
    
    if (Array.isArray(cachedUsers) && cachedUsers.length) {
        // List filter for unresolved alarms only
        const filter = {
            pageSize: 100,
            withTotalPages: true,
            resolved: false
        };

        try {
            cachedUsers.forEach(async (user) => {
                // Service user credentials
                let auth = new BasicAuth({ 
                    user:     user.name,
                    password: user.password,
                    tenant:   user.tenant
                });

                // Platform authentication
                let client = await new Client(auth, baseUrl);
        
                // Get filtered alarms and post a message to Slack
                let { data } = await client.alarm.list(filter);
                data.forEach((alarm) => {
                    postSlackMessage(alarm);
                });
        
                // Real time subscription for active alarms
                client.realtime.subscribe("/alarms/*", (alarm) => {
                    if (alarm.data.data.status === "ACTIVE") {
                        postSlackMessage(alarm.data.data);
                    }
                });
            });
            console.log("listening to alarms...");
        }
        catch (err) {
            console.error(err);
        }
    }
    else {
        console.log("[ERROR]: Not subscribed/authorized users found.");
    }

})();
