<#macro genGetterAndSetter propertyName,javaType>
	public ${javaType} get${propertyName?cap_first}() {
		return ${propertyName};
	}
	public void set${propertyName?cap_first}(${javaType} ${propertyName}) {
		this.${propertyName} = ${propertyName};
	}		
</#macro>