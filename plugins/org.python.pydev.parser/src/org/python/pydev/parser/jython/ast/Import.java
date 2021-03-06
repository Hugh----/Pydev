// Autogenerated AST node
package org.python.pydev.parser.jython.ast;

import org.python.pydev.parser.jython.SimpleNode;
import java.util.Arrays;

public final class Import extends stmtType {
    public aliasType[] names;

    public Import(aliasType[] names) {
        this.names = names;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(names);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Import other = (Import) obj;
        if (!Arrays.equals(names, other.names)) return false;
        return true;
    }
    @Override
    public Import createCopy() {
        return createCopy(true);
    }
    @Override
    public Import createCopy(boolean copyComments) {
        aliasType[] new0;
        if(this.names != null){
        new0 = new aliasType[this.names.length];
        for(int i=0;i<this.names.length;i++){
            new0[i] = (aliasType) (this.names[i] != null?
            this.names[i].createCopy(copyComments):null);
        }
        }else{
            new0 = this.names;
        }
        Import temp = new Import(new0);
        temp.beginLine = this.beginLine;
        temp.beginColumn = this.beginColumn;
        if(this.specialsBefore != null && copyComments){
            for(Object o:this.specialsBefore){
                if(o instanceof commentType){
                    commentType commentType = (commentType) o;
                    temp.getSpecialsBefore().add(commentType.createCopy(copyComments));
                }
            }
        }
        if(this.specialsAfter != null && copyComments){
            for(Object o:this.specialsAfter){
                if(o instanceof commentType){
                    commentType commentType = (commentType) o;
                    temp.getSpecialsAfter().add(commentType.createCopy(copyComments));
                }
            }
        }
        return temp;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Import[");
        sb.append("names=");
        sb.append(dumpThis(this.names));
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Object accept(VisitorIF visitor) throws Exception {
        return visitor.visitImport(this);
    }

    @Override
    public void traverse(VisitorIF visitor) throws Exception {
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                if (names[i] != null) {
                    names[i].accept(visitor);
                }
            }
        }
    }

}
