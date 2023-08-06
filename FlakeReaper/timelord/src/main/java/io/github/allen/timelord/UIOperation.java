package io.github.allen.timelord;

import java.util.List;

public class UIOperation {

    private Class<?> operationClass;

    private String methodName;

    private List<Class<?>> paramTypes;

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
