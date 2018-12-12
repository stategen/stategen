package org.stategen.framework.lite;

public interface ITreeNode<T extends ITreeNode<T>> {
    public T getParent();
    
    public void addChild(T child) ;

}
