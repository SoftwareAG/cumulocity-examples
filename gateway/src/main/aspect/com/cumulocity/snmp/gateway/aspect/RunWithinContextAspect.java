package com.cumulocity.snmp.gateway.aspect;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.snmp.model.core.Credentials;
import com.cumulocity.snmp.model.core.GatewayProvider;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.repository.configuration.ContextProvider;
import com.cumulocity.snmp.repository.configuration.ContextProvider.Callable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class RunWithinContextAspect {

    @Pointcut("@annotation(com.cumulocity.snmp.annotation.gateway.RunWithinContext)")
    public void withAnnotation() {
    }

    @Pointcut("execution(* *(..))")
    public void atExecution() {
    }

    @Around("withAnnotation() && atExecution() && args(credentials,..)")
    public Object runWithinContext(Credentials credentials, ProceedingJoinPoint jointPoint) throws Throwable {
        return doInvoke(credentials, jointPoint);
    }

    @Around("withAnnotation() && atExecution() && args(credentials,..)")
    public Object runWithinContext(GatewayProvider credentials, ProceedingJoinPoint jointPoint) throws Throwable {
        return doInvoke(credentials.getGateway(), jointPoint);
    }

    @Around("withAnnotation() && atExecution() && args(credentials,..)")
    public Object runWithinContext(DeviceCredentialsRepresentation credentials, ProceedingJoinPoint jointPoint) throws Throwable {
        return doInvoke(Gateway.gateway()
                .tenant(credentials.getTenantId())
                .name(credentials.getUsername())
                .password(credentials.getPassword())
                .build(), jointPoint);
    }

    private Object doInvoke(Credentials credentials, final ProceedingJoinPoint jointPoint) throws Throwable {
        return ContextProvider.doInvoke(credentials, new Callable<Object>() {
            @Override
            public Object call() throws Throwable {
                return jointPoint.proceed();
            }
        });
    }
}
