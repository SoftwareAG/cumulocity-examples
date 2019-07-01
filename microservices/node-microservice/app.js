"use strict";

require("dotenv").config();
const express = require("express");
const app = express();

// Application endpoints
const routes = require("./routes");
routes(app); 

// Server listening on port 80
app.use(express.json());
app.listen(process.env.PORT);
console.log(`${process.env.APPLICATION_NAME} started on port ${process.env.PORT}`);

// Cumulocity and Slack controllers
require("./controllers");
