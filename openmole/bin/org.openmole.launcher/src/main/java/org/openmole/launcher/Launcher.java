package org.openmole.launcher;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Romain Reuillon on 31/03/16.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Launcher {

    public static void main(String[] args) {

        //FIXME: Use options when OpenMOLE will enforce java 8
        File directory = null;
        String run = null;
        String osgiDirectory = null;
        List<String> priority = new LinkedList<String>();

        String[] forwardAgs = new String[0];

        int i = 0;
        while(i < args.length) {
            if(args[i].contentEquals("--plugins")) {
                i++;
                directory = new File(args[i]);
                i++;
                continue;
            } else if(args[i].contentEquals("--run")) {
                i++;
                run = args[i];
                i++;
                continue;
            } else if(args[i].contentEquals("--osgi-directory")) {
                i++;
                osgiDirectory = args[i];
                i++;
                continue;
            } else if(args[i].contentEquals("--priority")) {
                i++;
                priority.add(args[i]);
                i++;
            } else if(args[i].contentEquals("--")) {
                i++;
                forwardAgs = Arrays.copyOfRange(args, i, args.length);
                break;
            } else {
                i++;
                continue;
            }
        }

        FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();

        Map<String, String> osgiConfig = new HashMap<String, String>();
        osgiConfig.put(Constants.FRAMEWORK_STORAGE, "");
        osgiConfig.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
        osgiConfig.put(Constants.FRAMEWORK_BOOTDELEGATION, "*");
        osgiConfig.put(Constants.FRAMEWORK_SYSTEMCAPABILITIES, "osgi.ee; osgi.ee=\"JavaSE\";version:List=\"1.0,1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,1.10,1.11,1.12\"");
        osgiConfig.put(Constants.FRAMEWORK_EXECUTIONENVIRONMENT, "J2SE-1.12,JavaSE-1.12,J2SE-1.11,JavaSE-1.11,J2SE-1.10,JavaSE-1.10,J2SE-1.9,JavaSE-1.9,J2SE-1.8,JavaSE-1.8,J2SE-1.7,JavaSE-1.7,J2SE-1.6,JavaSE-1.6,J2SE-1.5,JavaSE-1.5,J2SE-1.4,JavaSE-1.4,J2SE-1.3,JavaSE-1.3,J2SE-1.2,,JavaSE-1.2,CDC-1.1/Foundation-1.1,CDC-1.0/Foundation-1.0,J2ME,OSGi/Minimum-1.1,OSGi/Minimum-1.0");
        if(osgiDirectory !=  null) osgiConfig.put(Constants.FRAMEWORK_STORAGE, osgiDirectory);

        Framework framework = frameworkFactory.newFramework(osgiConfig);


        int ret = 0;
        try {
            if(directory == null) throw new RuntimeException("Missing plugin directory argument");
            if(run == null) throw new RuntimeException("Missing run class argument");
            if(directory == null) throw new RuntimeException("Missing plugin directory argument");
            if(!directory.exists()) throw new RuntimeException("Plugin directory does not exist");

            framework.init();

            BundleContext context = framework.getBundleContext();

            List<Bundle> bundles = new LinkedList<>();

            for(File f: directory.listFiles()) {
                Bundle b = context.installBundle(f.toURI().toString());
                if(b.getSymbolicName() == null) throw new RuntimeException("Bundle " + f + " has no symbolic name");
                bundles.add(b);
            }

            for(String p: priority) {
                for(Bundle b: bundles) {
                    if(b.getSymbolicName().contains(p)) b.start();
                }
            }

            framework.start();

            for(Bundle b: bundles) {
                b.start();
            }

            Class main = null;

            for(Bundle b: bundles) {
                try {
                    Class c = b.loadClass(run);
                    main = c;
                } catch(Exception ignore) {}
            }

            if(main == null) throw new RuntimeException("Class " + run + " has not been found");

            Method runMethod = main.getDeclaredMethod("run", String[].class);
            if(!java.lang.reflect.Modifier.isStatic(runMethod.getModifiers())) throw new RuntimeException("Run method should be static");
            if(!int.class.isAssignableFrom(runMethod.getReturnType())) throw new RuntimeException("Run method should return int");

            ret = (int) runMethod.invoke(null, (Object) forwardAgs);

            framework.stop();
        } catch(Throwable e) {
            e.printStackTrace();
            System.exit(127);
        }

        System.exit(ret);
    }


}
