/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
function fn() {
	// don't waste time waiting for a connection or if servers don't respond within 5 seconds
	karate.configure('connectTimeout',1000000);
	karate.configure('readTimeout', 1000000);

	var port = karate.properties['server.port']; // getjava system property 'karate.properties'
	karate.log('server is running on port:', port);

	var config = { // base config JSON
		username : karate.properties['testuser'],
		password : karate.properties['testpass'],
		tenantId : karate.properties['tenantId']
	};

	config.baseUrl = "https://localhost:"+port+"/fineract-provider/api/v1/";

	var Faker = Java.type('com.github.javafaker.Faker');
    config.faker = new Faker();
    config.Base64 = Java.type('java.util.Base64');

    config.format = "dd MMMM yyyy";

    var DateFormat = Java.type('java.text.SimpleDateFormat');
    config.df = new DateFormat(config.format);

    var Calendar = Java.type('java.util.Calendar');
    config.calendar = Calendar.getInstance();

    config.TimeUnit = Java.type('java.util.concurrent.TimeUnit');

	return config;
}