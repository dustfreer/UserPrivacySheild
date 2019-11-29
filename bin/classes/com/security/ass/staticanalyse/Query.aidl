package com.security.ass.staticanalyse;


interface Query {
	boolean queryConnect(String ip, int port);
	boolean queryAdUrl(String url);
	boolean queryLocation();
	boolean queryContact();
	boolean tel_phone();
	boolean sendSMS();
}