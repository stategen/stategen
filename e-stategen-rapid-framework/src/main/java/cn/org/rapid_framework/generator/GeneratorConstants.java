package cn.org.rapid_framework.generator;

/**
 * 生成器常量,用于统一存放生成器可以配置的常量
 * 
 * @author badqiu
 */
public enum GeneratorConstants {
    GENERATOR_TOOLS_CLASS("generator_tools_class", null, "用于控制存放在生成器中的工具类,以便在模板中使用 StringUtils"),

    GENERATOR_INCLUDES("generator_includes", null, ""),
    GENERATOR_EXCLUDES("generator_excludes", null, ""),

    GENERATOR_SOURCE_ENCODING("generator_sourceEncoding", "UTF-8", "生成器模板的输入编码,默认UTF-8"),
    GENERATOR_OUTPUT_ENCODING("generator_outputEncoding", "UTF-8", "生成器模板生成的文件编码,默认UTF-8"),

    GENERATOR_REMOVE_EXTENSIONS("generator_removeExtensions", ".ftl,.vm",
            "生成器模板自动删除的模板后缀,默认值为: .ftl,.vm"),

    GG_IS_OVERRIDE("gg_isOverride", "false", "用于控制模板默认是否覆盖输出文件,默认值是false"),
    TABLE_REMOVE_PREFIXES("tableRemovePrefixes", null, "需要移除的表名前缀,示例值: t_,v_"),
    TABLE_NAME_SINGULARIZE("tableNameSingularize", "false",
            "用于控制表名是否自动转换为单数, 如 customers ==> customer. 默认值是 false"),
    GENERATOR_SQL_RESULTCLASS_SUFFIX("generator_sql_resultClass_suffix", "Result",
            "generate by sql中查询结果使用的后缀，默认值为: Result"),

    JDBC_USERNAME("jdbc_username", null, "数据源:用户名"),
    JDBC_PASSWORD("jdbc_password", null, "数据源:密码"),
    JDBC_DRIVER("jdbc_driver", null, "数据源:驱动"),
    JDBC_URL("jdbc_url", null, "数据源:URL"),
    JDBC_SCHEMA("jdbc_schema", null, "数据源:schema"),
    JDBC_CATALOG("jdbc_catalog", null, "数据源:catalog"),

    DATA_SOURCE_JNDI_NAME("data_source_jndi_name", null, "数据源的jndi名称,用于加载数据源"),

    DATABASE_TYPE("databaseType", null, "数据库类型，模板中引用可以用于控制如：分页语句的生成，不同的数据库生成不同的分页语法"),

    USE_INNER_XML_FOR_XML_PARSING("use_inner_xml_for_xml_parse", "true",
            "xml解析时,使用innerXML取一个节点的值,如果false则使用nodeValue. 用途: table.xml配置使用该配置来控制读取方式"),

    SQLPARSE_IGNORE_SQL_EXCEPTION_ERROR_CODES("sqlparse_ignore_sql_exception_error_codes", "",
            "SqlFactory在解析sql时,需要忽略的SQLException的错误码列表,使用逗号分隔多个error codes"), ;

    public final String code;
    public final String defaultValue;
    public final String desc;

    GeneratorConstants(String code, String defaultValue, String desc) {
        this.code = code;
        this.defaultValue = defaultValue;
        this.desc = desc;
    }

}
