package net.technicpack.notimefix.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import net.technicpack.notimefix.coremod.asm.IAsmEditor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.ASM5;

public class NoTimeFixClassTransformer implements IClassTransformer {
	@Override
	public byte[] transform(String s, String s1, byte[] bytes) {
		if (bytes == null)
			return null;
		
		for (IAsmEditor editor : NoTimeFixCoremod.editors) {
			if (s1.equals(editor.getClassName()))
				bytes = updateMethod(bytes, editor);
		}
		
		return bytes;
	}
	
	private byte[] updateMethod(byte[] bytes, IAsmEditor editor) {
		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode(ASM5);
		reader.accept(node, 0);
		
		boolean foundMethod = false;
		for (MethodNode method : node.methods) {
			if (method.name.equals(editor.getMethodName()) && method.desc.equals(editor.getMethodDesc())) {
				foundMethod = true;
				editor.edit(method);
			}
		}
		
		if (!foundMethod)
			throw new RuntimeException("NoTimeFix failed to find a method named " + editor.getMethodName()
					+ " in class " + editor.getClassName());
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		node.accept(writer);
		return writer.toByteArray();
	}
}
