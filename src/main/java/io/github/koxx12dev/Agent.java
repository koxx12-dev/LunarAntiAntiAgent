package io.github.koxx12dev;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

public class Agent {
    public static void premain(String args, Instrumentation inst){
        System.out.println("Patching Lunar Client Agent check...");

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String targetClassName, Class<?> targetClass, ProtectionDomain protectionDomain, byte[] buffer) {
                if(!targetClassName.startsWith("com/moonsworth/lunar/")) return buffer;

                ClassReader reader = new ClassReader(buffer);

                if(reader.getInterfaces().length != 0) return buffer;

                ClassNode node = new ClassNode();
                reader.accept(node, 0);

                for(MethodNode method : node.methods){
                    //extremely specific check, so it doesn't break anything else
                    if(method.name.equals("check") && method.desc.equals("()V") && method.access == (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)) {
                        System.out.println("Found Lunar Client Agent check method, patching... (" + targetClassName + ")");
                        InsnList inject = new InsnList();
                        inject.add(new InsnNode(Opcodes.RETURN));
                        method.instructions = inject;

                        ClassWriter writer = new ClassWriter(reader, 0);
                        node.accept(writer);
                        System.out.println("Successfully patched Lunar Client AntiAgent!");
                        return writer.toByteArray();
                    }
                }

                return buffer;
            }
        });
    }
}
