package gr.uom.java.metric.probability.browser;

import java.io.*;
import java.util.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class ClassBrowser {
    private SystemObject so;
    private List<File> fPackage;

    public ClassBrowser(File dir) {
        this.so = new SystemObject();
        fPackage = new ArrayList<File>();
        recurse(dir);
        so.organizeObjects();
    }

    private void recurse(File file) {
        if(file.isDirectory()) {
            File afile[] = file.listFiles();
            PackageObject po = parsePBytecode(file);
            so.addPackage(po);
            for(int i = 0; i < afile.length; i++) {
                if(afile[i].isDirectory())
                    recurse(afile[i]);
                else if(afile[i].getName().toLowerCase().endsWith(".class")) {
                	ClassObject co = parseBytecode(afile[i]);
                    so.addClass(co);
                }
            }
        }
        else if(file.getName().toLowerCase().endsWith(".class"))
            so.addClass(parseBytecode(file));
    }

    private ClassObject parseBytecode(File file) {
        final ClassObject co = new ClassObject();
        try {
            FileInputStream fin = new FileInputStream(file);
            ClassReader cr = new ClassReader(new DataInputStream(fin));

            ClassNode cn = new ClassNode();
    		cr.accept(cn, ClassReader.SKIP_DEBUG);

            String name = cn.name;
            co.setName(name.replaceAll("/", "."));

            String superClass = cn.superName;
            co.setSuperclass(superClass.replaceAll("/", "."));

            List interfaces = cn.interfaces;
            for (int i = 0; i < interfaces.size(); ++i) {
                String interfaceString = (String)interfaces.get(i);
                co.addInterface(interfaceString.replaceAll("/", "."));
            }

            List fields = cn.fields;
            for (int i = 0; i < fields.size(); ++i) {
                FieldNode field = (FieldNode)fields.get(i);
                Type fieldType = Type.getType(field.desc);
                co.addField(new FieldObject(fieldType.getClassName(),field.name, field.access));
            }

            List methods = cn.methods;
            for (int i = 0; i < methods.size(); ++i) {
                            	
            	MethodNode method = (MethodNode)methods.get(i);

            	if (method.name.equals("processingInstruction")) {
            		System.out.println("Method Found!!!");
            	}
            
                if(method.name.equals("<init>")) {
                    Type[] argumentTypes = Type.getArgumentTypes(method.desc);
                    ConstructorObject constructor = new ConstructorObject();
                    for(int j = 0; j < argumentTypes.length; j++)
                        constructor.addParameter(argumentTypes[j].getClassName());
                    co.addConstructor(constructor);
                }
                else {
                    Type[] argumentTypes = Type.getArgumentTypes(method.desc);
                    Type returnType = Type.getReturnType(method.desc);
                    MethodObject methodObject = new MethodObject(method.name);
                    methodObject.setReturnType(returnType.getClassName());
                    for(int j = 0; j < argumentTypes.length; j++)
                        methodObject.addParameter(argumentTypes[j].getClassName());
                    methodObject.setAccess(method.access);                   
                    methodObject.setLocalVariables(method.localVariables);
                    methodObject.setAttributes(method.attrs);
                    methodObject.setInstructions(method.instructions);
                    co.addMethod(methodObject);
                }

                if (method.instructions.size() > 0) {

      				for (int j = 0; j < method.instructions.size(); ++j) {

                        if(method.instructions.get(j) instanceof AbstractInsnNode) {
                        AbstractInsnNode ainsn = (AbstractInsnNode)method.instructions.get(j);

                        if( (ainsn.getOpcode() == Opcodes.INVOKEVIRTUAL) ||
                            (ainsn.getOpcode() == Opcodes.INVOKESTATIC) ||
                            (ainsn.getOpcode() == Opcodes.INVOKESPECIAL) ||
                            (ainsn.getOpcode() == Opcodes.INVOKEINTERFACE) ) {

                            MethodInsnNode minsn = (MethodInsnNode)ainsn;
                            MethodInvocationObject mio = new MethodInvocationObject(minsn.owner.replaceAll("/", "."),minsn.name, ainsn.getOpcode());

                            co.addMethodInvocation(mio);
                        }

                        if( (ainsn.getOpcode() == Opcodes.NEW) ||
                            (ainsn.getOpcode() == Opcodes.ANEWARRAY) ) {

                            TypeInsnNode tinsn = (TypeInsnNode)ainsn;
                            co.addObjectInstantiation(tinsn.desc.replaceAll("/", "."));
                        }
                        }
                    }
      			}
            }
            fin.close();
            /*System.out.println(co.getName() + " !!!");
            System.out.println("\n");*/
        }
        catch(FileNotFoundException fnfe) {}
        catch(IOException ioe) {}
        return co;
    }
    
    //TASK 3
    private PackageObject parsePBytecode(File file) {
        PackageObject po = new PackageObject();
        if(fPackage.isEmpty())
        	po.setName("default");
        else
        	po.setName(file.getName().replaceAll("/", "."));
        System.out.println(po.getName() + " Package");
        System.out.print("");
        fPackage.add(file);
        return po;
    }

    public SystemObject getSystemObject() {
        return so;
    }
}