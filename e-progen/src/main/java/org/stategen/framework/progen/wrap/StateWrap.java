/*
 * Copyright (C) 2018  niaoge<78493244@qq.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.stategen.framework.progen.wrap;

import java.util.Set;

import org.stategen.framework.enums.StateOperation;

/**
 * The Class StateWrap.
 */
public class StateWrap {

    private StateOperation operation;

    private Boolean init;

    private Boolean isSetted;

    private Boolean genEffect;

    private Boolean initCheck;

    private Set<String> areaExtraProps;

    private Set<String> stateExtraProps;

    public Boolean getInit() {
        return init;
    }

    public void setInit(Boolean init) {
        this.init = init;
    }

    public StateOperation getOperation() {
        return operation;
    }

    public void setOperation(StateOperation operation) {
        this.operation = operation;
    }

    public Boolean getIsSetted() {
        return isSetted;
    }

    public void setIsSetted(Boolean isSetted) {
        this.isSetted = isSetted;
    }

    public Boolean getGenEffect() {
        return genEffect;
    }

    public void setGenEffect(Boolean genEffect) {
        this.genEffect = genEffect;
    }

    public Set<String> getAreaExtraProps() {
        return areaExtraProps;
    }

    public void setAreaExtraProps(Set<String> areaExtraProps) {
        this.areaExtraProps = areaExtraProps;
    }

    public Set<String> getStateExtraProps() {
        return stateExtraProps;
    }

    public void setStateExtraProps(Set<String> stateExtraProps) {
        this.stateExtraProps = stateExtraProps;
    }

    public Boolean getInitCheck() {
        return initCheck;
    }
    
    public void setInitCheck(Boolean initCheck) {
        this.initCheck = initCheck;
    }

}
