package com.qdd.narutofix.core;


import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;
import java.util.function.Predicate;

public class FixTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("net.minecraft.client.gui.inventory.GuiInventory".equals(transformedName)) {
            return transformGuiInventory(basicClass);
        }
        if ("com.qdd.narutofix.gui.CustomGuiContainer".equals(transformedName)) {
            return transformCustomGuiContainer(basicClass);
        }

        return basicClass;
    }

    private byte[] transformGuiInventory(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
//
//        // 假设已通过 locateMethod 定位到一个已有方法（例如构造方法）
//        MethodNode mn = locateMethod(cn, "<init>");
//        MethodNode newMethod = new MethodNode(Opcodes.ACC_PUBLIC, "<GuiInventory>", "(Lcom/qdd/narutofix/gui/CustomContainer;)V", null, null);
//
//        newMethod.instructions.insert(new VarInsnNode(Opcodes.ALOAD, 1));
//        newMethod.instructions.insert(new MethodInsnNode(Opcodes.INVOKESPECIAL,
//                "net/minecraft/client/renderer/InventoryEffectRenderer",  // 父类是 Object
//                "<init>",            // 调用构造方法
//                "(Lcom/qdd/narutofix/gui/CustomContainer;)V",               // 构造方法描述符
//                false));
//
//        // 插入 this.allowUserInput = true;
//        // 1. 加载 this（当前对象）
//        newMethod.instructions.insert(new VarInsnNode(Opcodes.ALOAD, 0));  // this 是当前对象，索引 0
//        // 2. 加载 true
//        newMethod.instructions.insert(new LdcInsnNode(true));  // 将 true 加载到栈上
//        // 3. 设置字段 allowUserInput 为 true
//        newMethod.instructions.insert(new FieldInsnNode(Opcodes.PUTFIELD,
//                "net/minecraft/client/gui/inventory/GuiInventory",  // 替换成实际类名
//                "allowUserInput",         // 字段名
//                "Z"));
//        // 将新方法插入到类的 methods 列表中，确保插入到目标方法后面
//        int insertIndex = cn.methods.indexOf(mn) + 1;  // 找到目标方法后面的索引
//        cn.methods.add(insertIndex, newMethod);  // 在目标方法后插入新的方法


        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private byte[] transformCustomGuiContainer(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

//        MethodNode mn = locateMethod(cn, "<init>");
//        AbstractInsnNode node = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.NEW && n.getNext().getOpcode()==Opcodes.DUP);
//        mn.instructions.insert(node, new TypeInsnNode(Opcodes.NEW, "com/qdd/narutofix/gui/CustomContainer"));
//        mn.instructions.remove(node);
//        AbstractInsnNode initContainer = locateTargetInsn(mn, n -> n.getOpcode()==Opcodes.INVOKESPECIAL && ((MethodInsnNode)n).owner.equals("net/minecraft/entity/player/EntityPlayer") && ((MethodInsnNode)n).name.equals("<init>"));
//        mn.instructions.insert(initContainer, new MethodInsnNode(Opcodes.INVOKESPECIAL, "com/qdd/narutofix/gui/CustomContainer", "<init>", new String(((MethodInsnNode)initContainer).desc), false));
//        mn.instructions.remove(initContainer);


        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }




    private static MethodNode locateMethod(ClassNode cn, String desc, String nameIn) {
        return cn.methods.parallelStream()
                .filter(n -> n.desc.equals(desc) && (n.name.equals(nameIn)))
                .findAny().orElseThrow(() -> new ASMException(nameIn +": "+desc+" cannot be found in "+cn.name, cn));
    }

    private static MethodNode locateMethod(ClassNode cn, String nameIn) {
        return cn.methods.parallelStream()
                .filter(n -> n.name.equals(nameIn))
                .findAny()
                .orElseThrow(() -> new ASMException(nameIn+" cannot be found in "+cn.name, cn));
    }
    private static AbstractInsnNode locateTargetInsn(MethodNode mn, Predicate<AbstractInsnNode> filter) {
        AbstractInsnNode target = null;
        Iterator<AbstractInsnNode> i = mn.instructions.iterator();
        while (i.hasNext() && target == null) {
            AbstractInsnNode n = i.next();
            if (filter.test(n)) {
                target = n;
            }
        }
        if (target == null) {
            throw new ASMException("Can't locate target instruction in " + mn.name, mn);
        }
        return target;
    }
}
