package net.sf.sockettest.callbacks;

import net.sf.sockettest.vo.RuleEntity;

/**
 * Created by Srikanth on 2/19/2016.
 */
public interface OnEachEntity<T extends RuleEntity> {
    void onEachItem(T entity);
}
