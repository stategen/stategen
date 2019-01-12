package org.stategen.framework.lite.enums;

import org.stategen.framework.annotation.ReferConfig;

public abstract class EditorType {

    public static abstract class Password extends EditorType {

    }

    public static abstract class Input extends EditorType {

    }

    public static abstract class Hidden extends EditorType {

    }

    public static abstract class Textarea extends EditorType {

    }

    public static abstract class Number extends EditorType {

    }

    public static abstract class Search extends EditorType {

    }

    public static abstract class Switch extends EditorType {

    }

    public static abstract class Rate extends EditorType {

    }

    public static abstract class TimeStamp extends EditorType {

    }

    public static abstract class TimePicker extends EditorType {

    }

    public static abstract class DatePicker extends EditorType {

    }

    //以下类指定 @ReferConfig
    @ReferConfig
    private static abstract class HasReferFieldEditorType extends EditorType {
    }

    public static abstract class Checkbox extends HasReferFieldEditorType {

    }

    public static abstract class Select extends HasReferFieldEditorType {

    }

    public static abstract class Cascader extends HasReferFieldEditorType {

    }

    public static abstract class RadioGroup extends HasReferFieldEditorType {

    }

    public static abstract class CheckboxGroup extends HasReferFieldEditorType {

    }

    public static abstract class Upload extends HasReferFieldEditorType {

    }

    public static abstract class Image extends HasReferFieldEditorType {

    }

}
