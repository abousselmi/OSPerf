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

import com.rabbitmq.examples.PerfTest;

/**
 *
 * @author Ayoub Bousselmi
 * @since August 2016
 */
public class RPCEmulator implements Runnable {

    private final String[] perfTestArgs;
    
    public RPCEmulator(String[] args) {
        
        if(args != null) {
            perfTestArgs = new String[args.length];
            System.arraycopy(args, 0, perfTestArgs, 0, args.length );
        }else
            perfTestArgs = new String[]{"-?"};
    }
    
    @Override
    public void run() {
        PerfTest.main(perfTestArgs);
    }
    
    public void startRPCEmulation() {
        new Thread(RPCEmulator.this).start();
    }
    
}
