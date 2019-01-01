package org.stategen.framework.lite;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public abstract class TreeNode<T extends TreeNode<T>> implements ITreeNode<T> {

    protected transient T parent;

    protected transient String _uuid = UUID.randomUUID().toString();

    protected LinkedHashMap<String, T> _children;
    
    protected transient T root;

    public T getParent() {
        return parent;
    }

    /***不会被fastjson序列化，用 getChildren 或 getChildList 或其它方法 调用 get_Children */
    protected synchronized List<T> getChildren() {
        if (_children != null) {
            return new ArrayList<T>(_children.values());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addChild(T child) {
        if (child != null) {
            if (child.parent != null) {
                if (child.parent==this){
                    return;
                }
                if (child.parent._children != null) {
                    child.parent._children.remove(child._uuid);
                }
            }
            
            if (this._children == null) {
                this._children = new LinkedHashMap<String, T>();
            }
            child.parent=(T) this;
            this._children.put(child._uuid, child);
        }
    }
    
    @Override
    public void cleanChildren() {
        this._children=null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T getRoot() {
        T root = (T) this;
        while (true) {
            T parentOfParent =root.getParent();  
            if (parentOfParent!=null){
                root=parentOfParent; 
            } else {
                break;
            }
            
        }
        return root;
    }
    
}
