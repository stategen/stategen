package org.stategen.framework.lite.enums;

public abstract class EditorType {
    public boolean needReferConfig() {
        return false;
    }

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

    private static class HasReferFieldEditorType extends EditorType {
        @Override
        public boolean needReferConfig() {
            return true;
        }
    }

    public static class Checkbox extends HasReferFieldEditorType {

    }

    public static class Select extends HasReferFieldEditorType {

    }

    public static class Cascader extends HasReferFieldEditorType {

    }

    public static class RadioGroup extends HasReferFieldEditorType {

    }

    public static class CheckboxGroup extends HasReferFieldEditorType {

    }

    public static class Upload extends HasReferFieldEditorType {

    }

    public static class Image extends HasReferFieldEditorType {

    }

}
