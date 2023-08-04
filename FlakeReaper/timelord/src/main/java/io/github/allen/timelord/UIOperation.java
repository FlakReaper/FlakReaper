package io.github.allen.timelord;

import java.util.List;

public class UIOperation {

    /**
     * 操作类
     */
    private Class<?> operationClass;

    /**
     * 操作类中的方法名
     */
    private String methodName;

    /**
     * 方法名中的类型集合
     */
    private List<Class<?>> paramTypes;

    /**
     * 返回值类型
     */
    private Class<?> returnType;

    public Class<?> getOperationClass() {
        return operationClass;
    }

    public void setOperationClass(Class<?> operationClass) {
        this.operationClass = operationClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<Class<?>> getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(List<Class<?>> paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    @Override
    public String toString() {
        return "UIOperation{" +
                "operationClass=" + operationClass +
                ", methodName='" + methodName + '\'' +
                ", paramTypes=" + paramTypes +
                ", returnType=" + returnType +
                '}';
    }
}
