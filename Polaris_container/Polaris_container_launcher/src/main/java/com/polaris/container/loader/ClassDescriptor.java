package com.polaris.container.loader;

import java.lang.annotation.Annotation;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.SpringAsmInfo;
import org.springframework.asm.Type;

import com.polaris.core.annotation.PolarisApplication;
import com.polaris.core.annotation.PolarisWebApplication;

public class ClassDescriptor extends ClassVisitor{
	private static final Type STRING_ARRAY_TYPE = Type.getType(String[].class);
	private static final Type MAIN_METHOD_TYPE = Type.getMethodType(Type.VOID_TYPE, STRING_ARRAY_TYPE);

	private static final String MAIN_METHOD_NAME = "main";
	
	private static final Class<? extends Annotation> TARGET_ANNOTATION = PolarisApplication.class;
	private static final Class<? extends Annotation> TARGET_WEB_ANNOTATION = PolarisWebApplication.class;


	private boolean targetAnnotationFound;
	private boolean mainMethodFound;

	ClassDescriptor() {
		super(SpringAsmInfo.ASM_VERSION);
	}

	@Override
	public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		String className = Type.getType(desc).getClassName();
		if (className.equals(TARGET_ANNOTATION.getName()) || className.equals(TARGET_WEB_ANNOTATION.getName())) {
			targetAnnotationFound = true;
		} else {
			try {
				Class<?> clazz = Class.forName(className);
				if (clazz.isAnnotationPresent(TARGET_ANNOTATION) || clazz.isAnnotationPresent(TARGET_WEB_ANNOTATION) ) {
					targetAnnotationFound = true;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (isAccess(access, Opcodes.ACC_PUBLIC, Opcodes.ACC_STATIC) && MAIN_METHOD_NAME.equals(name)
				&& MAIN_METHOD_TYPE.getDescriptor().equals(desc)) {
			this.mainMethodFound = true;
		}
		return null;
	}

	private boolean isAccess(int access, int... requiredOpsCodes) {
		for (int requiredOpsCode : requiredOpsCodes) {
			if ((access & requiredOpsCode) == 0) {
				return false;
			}
		}
		return true;
	}

	boolean isMainMethodFound() {
		return this.mainMethodFound;
	}

	boolean isTargetAnnotationFound() {
		return this.targetAnnotationFound;
	}
	
    String convertToClassName(String name) {
		name = name.replace('/', '.');
		name = name.replace('\\', '.');
		return name;
	}

}
