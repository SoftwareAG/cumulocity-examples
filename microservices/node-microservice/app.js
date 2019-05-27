"use strict";

const express = require("express");
const app = express();
const port = process.env.PORT || 80;

// Application endpoints
const routes = require("./routes");
routes(app);

// Server listening on port 80
app.use(express.json());
app.listen(port);
console.log(`${process.env.APPLICATION_NAME} started on port ${port}`);

// Cumulocity and Slack controllers
const controllers = require("./controllers");
