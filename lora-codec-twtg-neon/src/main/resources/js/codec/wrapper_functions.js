/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2022 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// A wrapper function which takes a base16 encoded string (assuming Device presents a base16 encoded
// payload in the Uplink message), converts it into bytes, invokes the Decode() function
// from '/js/codec/decoder_ts_prot-2_doc-v2.2.1_rev-0' and returns the decoded JSON Object as a String.
//
// This wrapper function is created to overcome the limitations of Graaljs with
// its handling of multilevel json objects and certain other data types.
function DecodeForGraaljs(fPort, hexEncodedPayload) {
	// Returns serialized JSON object
	return JSON.stringify(Decode(fPort, toByteArray(hexEncodedPayload)));
}

function toByteArray(hexString) {
    var length = hexString.length;
    for (var bytes = [], c = 0; c < length; c += 2) {
        bytes.push(parseInt(hexString.substr(c, 2), 16));
    }

    return bytes;
}

// A wrapper function which in turn invokes the Encode() function from
// '/js/codec/encoder_ts_prot-2_doc-v2.2.1_rev-0' and returns a base16 encoded
// string (assuming that the Device accepts base16 encoded string in the Downlink message).
//
// This wrapper function is created to overcome the limitations of Graaljs with
// its handling of multilevel json objects and certain other data types.
function EncodeForGraaljs(fPort, jsonStringCommandToEncode) {
    // Converting the byte array into a Hex String assuming this is what the device accepts.
	return toHexString(Encode(fPort, JSON.parse(jsonStringCommandToEncode)));
}

function toHexString(byteArray) {
    var hexString = '';
    byteArray.forEach(function(byte) {
        hexString += ('0' + (byte & 0xFF).toString(16)).slice(-2);
    });

    return hexString;
}