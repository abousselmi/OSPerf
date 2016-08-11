/*
 * Copyright 2016 Orange.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orange.osperf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Ayoub Bousselmi
 * @since August 2016
 */
public class OSInstance implements Runnable {

    //instance info variable
    private final String instanceName;
    private String instanceStartTime;
            
    //AMQP messages sizes in Bytes
    //these values were calculated using a real OpenStack deployment. More 
    //details about this research can be found in our paper "towards a 
    //massively distributed IaaS operating system: composition and evaluation of
    //OpenStack" submitted to IEEE CSCN conference (2016).
    //
    //RPC size: CPU node heartbeats (0 VN 0 VM)
    private final int NOVA_OA         = 1890;
    private final int NOVA_OCAV       = 1890;
    private final int NOVA_SII        = 1000;
    private final int NOVA_REP        = 640;
    private final int NEUTRON_RS      = 1530;
    private final int NEUTRON_REP     = 200;
    //RPC size: Additional size (1 VN 1 VM)
    private final int NOVA_OA_ADD     = 110;
    private final int NOVA_OCAV_ADD   = 110;
    private final int NOVA_SII_ADD    = 210;
    private final int NOVA_REP_ADD    = 480;
    private final int NEUTRON_RS_ADD  = 0;
    private final int NEUTRON_REP_ADD = 40;
    
    //datacenter simulation variables. 
    //for example: the number of compute nodes in a datacenter.
    private int nbrCPUN;
    private int nbrVN;
    private int nbrVMcpuN;
    private int nbrVMVN;
    private int nbrXVN;

    /**
     * Constructor of an OpenStack instance.
     * @param osInstanceName the name of this instance
     * @param nbrCPUNodes the number of compute nodes
     * @param nbrVNets the number of virtual networks
     * @param nbrVMPerCPUNode the number of virtual machines per compute node
     * @param nbrVMPerNet the number of virtual machine per virtual network
     * @param nbrCrossVNet the number of cross-POP virtual networks
     */
    public OSInstance(String osInstanceName, int nbrCPUNodes, int nbrVNets,
            int nbrVMPerCPUNode, int nbrVMPerNet, int nbrCrossVNet) {
        initStartTime();
        instanceName = osInstanceName;
        nbrCPUN      = nbrCPUNodes;
        nbrVN        = nbrVNets;
        nbrVMcpuN    = nbrVMPerCPUNode;
        nbrVMVN      = nbrVMPerNet;
        nbrXVN       = nbrCrossVNet;
        
        //construct RPCs and launch RPCEmulators.
    }
    
    /**
     * Do not use this method directly.<br> Use startInstance() instead.
     */
    @Override
    public void run() {
        System.out.println("Start time: "+instanceStartTime);
        while(true){
            try {
                System.out.println(instanceName+" iterating..");
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("Start time"+instanceStartTime);
                System.out.println("Caught exception: "+ex);
            }
        }
    }
    
    /**
     * Starts this OpenStack instance
     */
    public void startInstance() {
        new Thread(OSInstance.this).start();
    }

    /**
     * @return the instance start time
     */
    public String getInstanceStartTime() {
        return instanceStartTime;
    }
    
    private void initStartTime() {
        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        Calendar cal = Calendar.getInstance();
        instanceStartTime = df.format(cal.getTime());
    }
}
