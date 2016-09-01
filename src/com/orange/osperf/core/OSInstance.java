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
package com.orange.osperf.core;

import com.orange.osperf.util.LogWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author Ayoub Bousselmi
 * @since August 2016
 */
public class OSInstance implements Runnable {
    
    public OSInstance(
            int osInstanceID,
            String nodeType,
            int nbrCPUNodes,
            int nbrVNets,
            int nbrVMPerCPUNode,
            String _h,
            String _i,
            String _z) {
        
        initStartTime();
        this.osInstanceName  = "instance_" + osInstanceID;
        this.nodeType        = nodeType;
        this.isCtrl          = nodeType.equals("ctrl");
        if (isCtrl)
            this.nbrCPUNodes = 1;
        else
            this.nbrCPUNodes = nbrCPUNodes;
        this.nbrVMPerCPUNode = nbrVMPerCPUNode;
        this._h              = "-h" + _h;
        this._i              = "-i" + _i;
        this._z              = "-z" + _z;
        
        initOutputLog();
        cmdArgs = initEmulationArgs();
    }

    @Override
    public void run() {
        if(isCtrl) {
            System.out.println(instanceStartTime
                    +", starting \"ctrl\" node for instance \"" 
                    + osInstanceName+"\"");
        }else {
            System.out.println(instanceStartTime+", starting " + nbrCPUNodes +
                    " \"cpu\" node" + (nbrCPUNodes > 1 ? "s" : "") + " for"
                    + " instance \"" + osInstanceName+"\"");
        }
        for (int i = 0; i < nbrCPUNodes; i++) {
            for (int j = 0; j < 6; j++) {
                try {
                    new RPCEmulator(cmdArgs.get(i).get(j)).startRPCEmulation();
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Caught exception: "+ex);
                }
            }
        }
    }

    public void startInstance() {
        new Thread(OSInstance.this).start();
    }

    public String getInstanceStartTime() {    
        return instanceStartTime;
    }
    
    private void initOutputLog() {
        String osPerfLogfilePathEnv = System.getenv().get("OSPERF_LOG");
        if(osPerfLogfilePathEnv == null) {
            System.out.println("OSPERF_LOG env variable is not set. For "
                    + "example:\neg.: OSPERF_LOG=/home/user/");
            System.exit(1);
        } else {
            LogWriter lw = null;
            try {
                lw = new LogWriter(osPerfLogfilePathEnv + "os-perf-" + 
                        osInstanceName + "_node_" + nodeType + ".log");
                PrintStream ps = new PrintStream(lw);
                System.setOut(ps);
            } catch (IOException ex) {
                System.out.println("IOException: "+ex);
            } finally {
                try {
                    if(lw != null)
                        lw.close();
                } catch (IOException ex) {
                    System.out.println("IOException" + ex);
                }
            }
        }
    }
    
    private void initStartTime() {
        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        Calendar cal = Calendar.getInstance();
        instanceStartTime = df.format(cal.getTime());
    }

    private ArrayList<ArrayList<String[]>> initEmulationArgs() {
        ArrayList<ArrayList<String[]>> args = new ArrayList<>();
        for (int i = 0; i < nbrCPUNodes; i++) {
            ArrayList<String[]> cpuArgs = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                if (isCtrl) {
                    _x += ""+ctrlProducerConsumerConfig[j][0];
                    _y += ""+ctrlProducerConsumerConfig[j][1];
                }else {
                    _x += ""+cpuProducerConsumerConfig[j][0];
                    _y += ""+cpuProducerConsumerConfig[j][1];
                }
                String[] arg = new String[]{_a,
                    _d + osInstanceName + "_" + (i+1) + "_" + RK_LIST[j], _c,
                    _f, _h, _i, _z, _e + X_NAME_LIST[j] + "_" + osInstanceName,
                    _k + RK_LIST[j], _r + getProducerRate(j), _s 
                        + getMessageSize(j), _t + X_TYPE_LIST[j], _u 
                        + Q_NAME_LIST[j] + "_"+ osInstanceName + "_"
                        + (i+1), _x, _y
                };
                cpuArgs.add(arg);
                initXY();
            }
            args.add(cpuArgs);
        }
        return args;
    }

    private String getProducerRate(int i) {
        if (isCtrl) {
            if(nbrVMPerCPUNode==0)
                return "" + RPC_RATES_0[i] * nbrCPUNodes;
            else
                return "" + (RPC_RATES_A[i] * nbrVMPerCPUNode + RPC_RATES_B[i]) 
                        * nbrCPUNodes;
        }else {
            if(nbrVMPerCPUNode==0)
                return "" + RPC_RATES_0[i];
            else
                return "" + (RPC_RATES_A[i] * nbrVMPerCPUNode + RPC_RATES_B[i]);
        }
        
    }

    private String getMessageSize(int i) {
        if(nbrVMPerCPUNode==0)
            return "" + Math.round(RPC_SIZES_0[i]);
        else {
            if(i==3) {
                return "" + Math.round((RPC_SIZES_A[i] * Math.pow(
                        nbrVMPerCPUNode, RPC_SIZES_B[i])));
            } else {
                return "" + Math.round((RPC_SIZES_A[i] * nbrVMPerCPUNode + 
                        RPC_SIZES_B[i]));
            }
        }
    }

    private void initXY() {
        _x = "-x";
        _y = "-y";
    }
    
    //init instance variables
    private final String _h;
    private final String _i;
    private final String _z;
    
    private final String _a = "-a";
    private final String _c = "-c1";
    private final String _f = "-fmandatory";
    private final String _d = "-d";
    private final String _e = "-e";
    private final String _k = "-k";
    private final String _r = "-r";
    private final String _s = "-s";
    private final String _t = "-t";
    private final String _u = "-u";
    
    private String _x       = "-x";
    private String _y       = "-y";

    private final int nbrCPUNodes;
    private final int nbrVMPerCPUNode;
    
    private final String osInstanceName;
    private String instanceStartTime;
    private final boolean isCtrl;
    private final String nodeType;
    private final ArrayList<ArrayList<String[]>> cmdArgs;
    
    private final float[] RPC_SIZES_0 = new float[]{
        2230f,
        1842f,
        1168f,
        549f,
        1821f,
        409f
    };

    private final float[] RPC_SIZES_A = new float[]{
        56.2f,
        7.3f,
        44.0f,
        1169.3f,
        0,
        93.6f
    };
    private final float[] RPC_SIZES_B = new float[]{
        2399.7f,
        1936.2f,
        1818.9f,
        0.4524f,
        1821.0f,
        263.1f
    };

    private final float[] RPC_RATES_0 = new float[]{
        0.117f,
        0.083f,
        0.008f,
        0.2f,
        0.033f,
        0.033f
    };

    private final float[] RPC_RATES_A = new float[]{
        0.008f,
        0.004f,
        0.0f, 
        0.013f,
        0.0f,
        0.002f
    };
    private final float[] RPC_RATES_B = new float[]{
        0.158f,
        0.133f,
        0.008f,
        0.287f,
        0.033f,
        0.033f
    };
    
    private final String[] RK_LIST = new String[]{
        "conductor-oa", 
        "conductor-ocav",
        "scheduler-sii",
        "reply-nova",
        "q-report-plugin-rs", 
        "relpy-neutron"
    };
    private final String[] X_TYPE_LIST = new String[]{
        "topic",
        "topic", 
        "fanout",
        "direct",
        "topic",
        "direct"
    };
    private final String[] X_NAME_LIST = new String[]{
        "nova",
        "nova", 
        "scheduler_fanout",
        "reply_nova",
        "neutron",
        "relpy_neutron"
    };
    private final String[] Q_NAME_LIST = new String[]{
        "compute_oa",
        "compute_ocav",
        "scheduler_sii",
        "reply_nova",
        "network_rs",
        "relpy_neutron"
    };
    
    private final int[][] ctrlProducerConsumerConfig = {
        {0,1},
        {0,1},
        {0,1},
        {1,0},
        {0,1},
        {1,0},
    };
    
    private final int[][] cpuProducerConsumerConfig = {
        {1,0},
        {1,0},
        {1,0},
        {0,1},
        {1,0},
        {0,1},
    };
}
