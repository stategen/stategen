package org.stategen.framework.lite;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeNode<T extends TreeNode<T>> {
    
    protected transient T parent;
    
    protected List<T> _children;

    public T getParent() {
        return parent;
    }
    
    /***不会被fastjson序列化，用 getChildren 或 getChildList 或其它方法 调用 get_Children */
    public List<T> get_Children() {
        return _children;
    }
        
    @SuppressWarnings("unchecked")
    public void setParent(T parent) {
        T oldParent = this.parent;
        if (oldParent!=null){
            if (oldParent!=parent){
                List<T> children = oldParent.get_Children();
                if (children!=null){
                    children.remove(this);
                }
            }
        }
        this.parent=parent;
        if (parent!=null){
            List<T> children = parent.get_Children();
            if (children==null){
                children=new ArrayList<T>();
                parent._children=children;
            }
            children.add((T)this);
            
        }
    }
}
