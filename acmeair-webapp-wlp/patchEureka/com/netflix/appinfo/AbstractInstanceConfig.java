/*
 * Copyright 2012 Netflix, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.netflix.appinfo;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.discovery.shared.Pair;

/**
 * An abstract instance info configuration with some defaults to get the users
 * started quickly.The users have to override only a few methods to register
 * their instance with eureka server.
 * 
 * @author Karthik Ranganathan
 * 
 */
public abstract class AbstractInstanceConfig implements EurekaInstanceConfig {
    private static final Logger logger = LoggerFactory
    .getLogger(AbstractInstanceConfig.class);
    
    private static final int LEASE_EXPIRATION_DURATION_SECONDS = 90;
    private static final int LEASE_RENEWAL_INTERVAL_SECONDS = 30;
    private static final boolean SECURE_PORT_ENABLED = false;
    private static final boolean NON_SECURE_PORT_ENABLED = true;
    private static final int NON_SECURE_PORT = 80;
    private static final int SECURE_PORT = 443;
    private static final boolean INSTANCE_ENABLED_ON_INIT = false;
    private static final String UNKNOWN_APPLICATION = "unknown";
    private static final Pair<String, String> hostInfo = getHostInfo();
    private DataCenterInfo info = new DataCenterInfo() {
        @Override
        public Name getName() {
            return Name.MyOwn;
        }
    };

    protected AbstractInstanceConfig() {

    }

    protected AbstractInstanceConfig(DataCenterInfo info) {
        this.info = info;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.netflix.appinfo.InstanceConfig#getAppname()
     */
    @Override
    public abstract String getAppname();

    /*
     * (non-Javadoc)
     * 
     * @see com.netflix.appinfo.InstanceConfig#isInstanceEnabledOnit()
     */
    @Override
    public boolean isInstanceEnabledOnit() {
        return INSTANCE_ENABLED_ON_INIT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.netflix.appinfo.InstanceConfig#getNonSecurePort()
     */
    @Override
    public int getNonSecurePort() {
        return NON_SECURE_PORT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.netflix.appinfo.InstanceConfig#getSecurePort()
     */
    @Override
    public int getSecurePort() {
        return SECURE_PORT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.netflix.appinfo.InstanceConfig#isNonSecurePortEnabled()
     */
    @Override
    public boolean isNonSecurePortEnabled() {
        return NON_SECURE_PORT_ENABLED;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.netflix.appinfo.InstanceConfig#getSecurePortEnabled()
     */
    @Override
    public boolean getSecurePortEnabled() {
        // TODO Auto-generated method stub
        return SECURE_PORT_ENABLED;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.netflix.appinfo.InstanceConfig#getLeaseRenewalIntervalInSeconds()
     */
    @Override
    public int getLeaseRenewalIntervalInSeconds() {
        return LEASE_RENEWAL_INTERVAL_SECONDS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.netflix.appinfo.InstanceConfig#getLeaseExpirationDurationInSeconds()
     */
    @Override
    public int getLeaseExpirationDurationInSeconds() {
        return LEASE_EXPIRATION_DURATION_SECONDS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.netflix.appinfo.InstanceConfig#getVirtualHostName()
     */
    @Override
    public String getVirtualHostName() {
        return (getHostName(false) + ":" + getNonSecurePort());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.netflix.appinfo.InstanceConfig#getSecureVirtualHostName()
     */
    @Override
    public String getSecureVirtualHostName() {
        return (getHostName(false) + ":" + getSecurePort());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.netflix.appinfo.InstanceConfig#getASGName()
     */
    @Override
    public String getASGName() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.netflix.appinfo.InstanceConfig#getHostName()
     */
    @Override
    public String getHostName(boolean refresh) {
        return hostInfo.second();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.netflix.appinfo.InstanceConfig#getMetadataMap()
     */
    @Override
    public Map<String, String> getMetadataMap() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.netflix.appinfo.InstanceConfig#getDataCenterInfo()
     */
    @Override
    public DataCenterInfo getDataCenterInfo() {
        // TODO Auto-generated method stub
        return info;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.netflix.appinfo.InstanceConfig#getIpAddress()
     */
    @Override
    public String getIpAddress() {
        return hostInfo.first();
    }

    private static Pair<String, String> getHostInfo() {
        Pair<String, String> pair = new Pair<String, String>("", "");
        try {
        	String ipAddr = getIpAddress("eth0");
            /*pair.setFirst(InetAddress.getLocalHost().getHostAddress());
            pair.setSecond(InetAddress.getLocalHost().getHostName());*/
        	
            pair.setFirst(ipAddr);
            pair.setSecond(ipAddr);

        } catch (UnknownHostException e) {
            logger.error("Cannot get host info", e);
        }
        return pair;
    }

	private static String getIpAddress(String nicName) throws UnknownHostException{

		String result =  null;

		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback() || !networkInterface.isUp())
					continue;
				System.out.println(networkInterface);
				Enumeration<InetAddress> addresses = networkInterface
						.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress address = addresses.nextElement();
					if (address.isLoopbackAddress()
							|| address instanceof Inet6Address)
						continue;
					result = address.getHostAddress();
					if(nicName.equals(networkInterface.getName()))
							break;
				}
			}
			
		} catch (Exception ex) {
			logger.error("Cannot discover ipaddress for " + nicName, ex);
		}
		
		if(result == null)
		{
			result = InetAddress.getLocalHost().getHostAddress();
		}

		return result;

	}
}