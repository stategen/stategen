package cn.org.rapid_framework.generator;

import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.stategen.framework.generator.BaseTargets;
import org.stategen.framework.generator.util.GenNames;
import org.stategen.framework.generator.util.GenProperties;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.StringUtil;

public class ConsoleGenerator {
    /**
     * 读取控制台内容
     */
    public static String consoleInput(String tip) {
        System.out.println(tip);
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        if (scanner.hasNext()) {
            String tableNames = scanner.next();
            if (StringUtil.isNotEmpty(tableNames)) {
                return tableNames;
            }
        }
        return null;
    }

    public void generate() throws Exception {
        String consoleInput  = null;
        String executeTarget = null;
        String tableName     = null;
        while (StringUtil.isBlank(consoleInput)) {
            consoleInput = consoleInput("请输入命令: dal|table 表名(如 dal user):");
            boolean      find   = false;
            List<String> inputs = CollectionUtil.toList(consoleInput, " ");
            if (inputs.size() < 2) {
                consoleInput = null;
                continue;
            }

            int idx = 0;
            executeTarget = inputs.get(idx);
            if ("gen.sh".equals(executeTarget)) {
                if (inputs.size() < 3) {
                    consoleInput = null;
                    continue;
                }

                idx++;
                executeTarget = inputs.get(idx);
            }

            if (executeTarget.equals("dal") || executeTarget.equals("table")) {
                System.setProperty(GenNames.executeTarget, executeTarget);
                idx++;
                tableName = inputs.get(idx);
                if (tableName != null) {
                    System.setProperty(GenNames.tableName, tableName);
                    find = true;
                }
            }

            if (!find) {
                consoleInput = null;
            }

        }

        Properties mergedProps = GenProperties.getAllMergedProps(GenProperties.getGenConfigXmlIfRunTest());
        GenProperties.putStatics(mergedProps);
        Properties pts = GeneratorProperties.getProperties();
        pts.putAll(mergedProps);

        BaseTargets baseTargets = new BaseTargets(null);

        if (executeTarget.equals("dal")) {
            baseTargets.dal();
        } else if (executeTarget.equals("table")) {
            baseTargets.table();
        }
        System.out.println(baseTargets+" "+tableName+" 执行完毕！");
    }

}
