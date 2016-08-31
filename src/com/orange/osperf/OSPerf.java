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

import com.orange.osperf.core.OSInstance;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Ayoub Bousselmi
 * @since August 2016
 */
public class OSPerf {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Options options = getOptions();
        CommandLineParser parser  = new GnuParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if(cmd.hasOption('?')){
                usage(options);
                System.exit(0);
            }
            
            int osInstanceID = getIntArg(cmd, 'i', 1);
            String nodeType = getStrArg(cmd, 't', "ctrl");
            int nbrCPUNodes = getIntArg(cmd, 'c', 1);
            int nbrVNets = getIntArg(cmd, 'n', -1);
            int nbrVMPerCPUNode = getIntArg(cmd, 'v', 1);
            String hostip = getStrArg(cmd, 'h', "localhost");
            String user = getStrArg(cmd, 'u', "guest");
            String pass = getStrArg(cmd, 'p', "guest");
            String statsInterval = getStrArg(cmd, 's', "1");
            String testDuration = getStrArg(cmd, 'd', "60");
            
            String connectionURI = "amqp://" + user + ":" + pass + "@" + hostip;
            
            //create and start an OpenStack node emulation instance
            OSInstance osi = new OSInstance(osInstanceID, nodeType, 
                    nbrCPUNodes, nbrVNets, nbrVMPerCPUNode,
                    connectionURI, statsInterval, testDuration);
            osi.startInstance();
            
        } catch (ParseException ex) {
            System.out.println("Parsing failed. Caught exception: " 
                    + ex.getMessage());
            usage(options);
        }
        
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption(new Option("?", "help", false, "show usage"));
        options.addOption(new Option("i", "id", true, "instance ID"));
        options.addOption(new Option("t", "type", true, "node type (ctrl or cpu)"));
        options.addOption(new Option("c", "cpuNbr", true, "cpu nodes count"));
        options.addOption(new Option("n", "netNbr", true, "virtual networks count"));
        options.addOption(new Option("v", "vmNbr", true, "virtual machines per cpu node count"));
        options.addOption(new Option("h", "host", true, "rabbitmq host ip"));
        options.addOption(new Option("u", "user", true, "rabbitmq username"));
        options.addOption(new Option("p", "pass", true, "rabbitmq password"));
        options.addOption(new Option("s", "sinterval", true, "stats sampling interval"));
        options.addOption(new Option("d", "duration", true, "test duration"));

        return options;
    }
    
    private static String getStrArg(CommandLine cmd, char opt, String def) {
        return cmd.getOptionValue(opt, def);
    }
    
    private static int getIntArg(CommandLine cmd, char opt, int def) {
        return Integer.parseInt(cmd.getOptionValue(opt, Integer.toString(def)));
    }

    private static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("os-perf takes the following arguments:", options);
    }
}
