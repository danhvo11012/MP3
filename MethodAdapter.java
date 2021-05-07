import java.io.IOException;

import org.objectweb.asm.*;

public class MethodAdapter extends MethodVisitor implements Opcodes {
    private int access = 0;
    
    public MethodAdapter(MethodVisitor mv, int access) {
        super(ASM5,mv);
        this.access = access; // Tweaked to check for isStatic() and isSynchronized()
    }
    
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
    	switch (opcode) {
        	case INVOKEVIRTUAL:
        		//check if it is "Thread.start()"
        		if(isThreadClass(owner)&&name.equals("start")&&desc.equals("()V")) {
	            	mv.visitInsn(DUP);
	        		mv.visitMethodInsn(INVOKESTATIC, "Log", "logStart", "(Ljava/lang/Thread;)V",false);
				}
        		
        		// Part 1
        		//check if it is "Thread.join()"
        		else if(isThreadClass(owner)&&name.equals("join")&&desc.equals("()V")) {
        			mv.visitInsn(DUP);
	        		mv.visitMethodInsn(INVOKESTATIC, "Log", "logJoin", "(Ljava/lang/Thread;)V",false);
    			} 
        		
        		//check if it is "Object.wait()"
            	else if(name.equals("wait")&&
                				(desc.equals("()V")||desc.equals("(J)V")||desc.equals("(JI)V"))) {
        			mv.visitInsn(DUP);
	        		mv.visitMethodInsn(INVOKESTATIC, "Log", "logWait", "(Ljava/lang/Object;)V",false);
        		} 
        		
        		//check if it is "Object.notify()"
                else if(name.equals("notify")&&desc.equals("()V")) {
                	mv.visitInsn(DUP);
	        		mv.visitMethodInsn(INVOKESTATIC, "Log", "logNotify", "(Ljava/lang/Object;)V",false);
            	}
        		
        		//check if it is "Object.notifyAll()"
                else if(name.equals("notifyAll")&&desc.equals("()V")) {
                	mv.visitInsn(DUP);
	        		mv.visitMethodInsn(INVOKESTATIC, "Log", "logNotifyAll", "(Ljava/lang/Object;)V",false);
                }
        		
        	default: mv.visitMethodInsn(opcode, owner, name, desc,itf);
    	}

    }
    
	private boolean isSynchronized() {
		return (access & Opcodes.ACC_SYNCHRONIZED) != 0;
	}
	
	private boolean isStatic() {
		return (access & Opcodes.ACC_STATIC) != 0;
	}
	
	// Part 1
    @Override
    public void visitInsn(int opcode) {
        switch (opcode) {
        	// Part 1
	    	case Opcodes.MONITORENTER:
	    	    mv.visitInsn(DUP);
	    	    mv.visitMethodInsn(INVOKESTATIC, "Log", "logLock","(Ljava/lang/Object;)V",false);
	    	    break;
	    	    
	    	case Opcodes.MONITOREXIT:
	    	    mv.visitInsn(DUP);
	    	    mv.visitMethodInsn(INVOKESTATIC, "Log", "logUnlock","(Ljava/lang/Object;)V",false);
	    	    break;
	    	    
	    	case IRETURN:
	    	case LRETURN:
	    	case FRETURN:
	    	case DRETURN:
	    	case ARETURN:
	    	case RETURN:
			    	    		
	    	case ATHROW:
	    	{
	    		if(isSynchronized()){
		    		if(isStatic()){
			    		mv.visitInsn(Opcodes.ACONST_NULL);
			    		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log", "logUnlock","(Ljava/lang/Object;)V",false);
		    		}
		    		else{
			    		mv.visitVarInsn(Opcodes.ALOAD, 0);
			    		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log", "logUnlock","(Ljava/lang/Object;)V",false);
		    		}
	    		}
	    	}
	    	break;
	    	
	    	// Part 2
	    	case AALOAD:case BALOAD:case CALOAD:case SALOAD:case IALOAD:case FALOAD:case
	    	DALOAD:case LALOAD:
		    	mv.visitVarInsn(ALOAD, 0);
		    	mv.visitLdcInsn(0); // Index 0
		    	mv.visitLdcInsn(0);	// Read access
	    	    mv.visitMethodInsn(INVOKESTATIC, "Log", "logArrayAcc","(Ljava/lang/Object;IZ)V",false);
	    		break;
	    		
	    	case AASTORE:case BASTORE:case CASTORE:case SASTORE:case IASTORE:case FASTORE:
		    	mv.visitVarInsn(ALOAD, 0);
		    	mv.visitLdcInsn(1);	// Index 1
		    	mv.visitLdcInsn(1);	// Write access
	    	    mv.visitMethodInsn(INVOKESTATIC, "Log", "logArrayAcc","(Ljava/lang/Object;IZ)V",false);
	    	    break;
	    	    
	    	case DASTORE:case LASTORE:
		    	mv.visitVarInsn(ALOAD, 0);
		    	mv.visitLdcInsn(1);	// Index 1
		    	mv.visitLdcInsn(1);	// Write access
	    	    mv.visitMethodInsn(INVOKESTATIC, "Log", "logArrayAcc","(Ljava/lang/Object;IZ)V",false);
	    	    break;
	    	
	    	default:break;
        }
        mv.visitInsn(opcode);
    }

    
    // Part 2
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
	    switch (opcode) {
		    case GETSTATIC:
		    	mv.visitLdcInsn(owner);
		    	mv.visitLdcInsn(name);
		    	mv.visitLdcInsn(1);
		    	mv.visitLdcInsn(0);
	    	    mv.visitMethodInsn(INVOKESTATIC, "Log", "logFieldAcc","(Ljava/lang/Object;Ljava/lang/String;ZZ)V",false);
	    	    break;
	    	    
		    case PUTSTATIC:
		    	mv.visitLdcInsn(owner);
		    	mv.visitLdcInsn(name);
		    	mv.visitLdcInsn(1);
		    	mv.visitLdcInsn(1);
	    	    mv.visitMethodInsn(INVOKESTATIC, "Log", "logFieldAcc","(Ljava/lang/Object;Ljava/lang/String;ZZ)V",false);
	    	    break;
		    	
		    case GETFIELD:
		    	mv.visitLdcInsn(owner);
		    	mv.visitLdcInsn(name);
		    	mv.visitLdcInsn(0);
		    	mv.visitLdcInsn(0);
	    	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log", "logFieldAcc","(Ljava/lang/Object;Ljava/lang/String;ZZ)V",false);
	    	    break;
			    
		    case PUTFIELD:
		    	mv.visitLdcInsn(owner);
		    	mv.visitLdcInsn(name);
		    	mv.visitLdcInsn(0);
		    	mv.visitLdcInsn(1);
	    	    mv.visitMethodInsn(INVOKESTATIC, "Log", "logFieldAcc","(Ljava/lang/Object;Ljava/lang/String;ZZ)V",false);			    
		    default: break;
	    }
	    mv.visitFieldInsn(opcode, owner, name, desc);
    }
    
    private boolean isThreadClass(String cname)
    {
    	while(!cname.equals("java/lang/Object"))
    	{
    		if(cname.equals("java/lang/Thread"))
    			return true;

    		try {
				ClassReader cr= new ClassReader(cname);
				cname = cr.getSuperName();
			} catch (IOException e) {
				return false;
			}
    	}
    	return false;
    }
    
}