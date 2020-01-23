package com.polaris.loader;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.SpringAsmInfo;
import org.springframework.asm.Type;

public class ClassDescriptor extends ClassVisitor{
	private static final Type STRING_ARRAY_TYPE = Type.getType(String[].class);
	private static final Type MAIN_METHOD_TYPE = Type.getMethodType(Type.VOID_TYPE, STRING_ARRAY_TYPE);

	private static final String MAIN_METHOD_NAME = "main";

	private final Set<String> annotationNames = new LinkedHashSet<>();
	private final Set<String> interfaceNames = new LinkedHashSet<>();

	private boolean mainMethodFound;

	ClassDescriptor() {
		super(SpringAsmInfo.ASM_VERSION);
	}

	@Override
	public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
		if (interfaces != null) {
			for (String intefaceName : interfaces) {
				interfaceNames.add(convertToClassName(intefaceName));
			}
		}
        super.visit(version, access, name, signature, superName, interfaces);
    }
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		this.annotationNames.add(Type.getType(desc).getClassName());
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

	Set<String> getAnnotationNames() {
		return this.annotationNames;
	}
	Set<String> getInterfaceNames() {
		return this.interfaceNames;
	}
	
    String convertToClassName(String name) {
		name = name.replace('/', '.');
		name = name.replace('\\', '.');
		return name;
	}

}
