package com.xxmrk888ytxx.settingsscreen.models

import androidx.annotation.IdRes

/**
 * [Ru]
 * Даннай класс является моделью данных для параметров в настройках
 * Подклассы представляют типы пораметров.
 * Описание базовых параметров
 * @param text - Текст описании настройки
 * @param icon - id ресурса иконки
 * @param isEnable - пораметр отвечает за то, доступен ли элемент
 * @param isVisible - параметр отвечает за то, видел ли элемент
 */
/**
 * [En]
 * This class is a data model for parameters in settings
 * Subclasses represent parameter types.
 * Description of basic parameters
 * @param text - Text describing the setting
 * @param icon - icon resource id
 * @param isEnable - parameter is responsible for whether the element is available
 * @param isVisible - the parameter is responsible for whether the element has been seen
 */
internal sealed class SettingsParamType(
    open val text:String,
    @IdRes open val icon:Int,
    open val isEnable:Boolean,
    open val isVisible:Boolean
) {

    /**
     * [Ru]
     * Данная модель предстовляет параметр настроек, с переключателем
     * Необходимые параметры помимо базовых:
     * @param isSwitched - включен ли переключатель
     * @param onStateChanged - лямбда которая должна вызываться, при изменении состоияния данного
     * элемента
     */
    /**
     * [En]
     * This model introduces a setting option, with a switch
     * Required parameters in addition to the basic ones:
     * @param isSwitched - whether the switch is enabled
     * @param onStateChanged - lambda that should be called when the state of this state changes
     * item
     */
    data class Switch(
        override val text:String,
        @IdRes override val icon:Int,
        val isSwitched: Boolean,
        override val isEnable: Boolean = true,
        override val isVisible: Boolean = true,
        val onStateChanged:(Boolean) -> Unit
    ) : SettingsParamType(text,icon,isEnable,isVisible)

    /**
     * [Ru]
     * Данная модель предстовляет параметр настроек, который реагирует на нажатие
     * Необходимые параметры помимо базовых:
     * @param onClick - действие которое будет выполнено при нажатии
     */
    /**
     * [En]
     * This model introduces a setting option that responds to pressing
     * Required parameters in addition to the basic ones:
     * @param onClick - action to be performed on click
     */
    data class Button(
        override val text:String,
        @IdRes override val icon:Int,
        override val isEnable: Boolean = true,
        override val isVisible: Boolean = true,
        val onClick:() -> Unit
    ) : SettingsParamType(text,icon,isEnable,isVisible)

    /**
     * [Ru]
     * Данная модель предстовляет параметр настроек, в котором отображается дополнительная инфармация
     * Необходимые параметры помимо базовых:
     * @param secondaryText - дополнительный текст
     */
    /**
     * [En]
     * This model introduces a settings option that displays additional information
     * Required parameters in addition to the basic ones:
     * @param secondaryText - additional text
     */
    data class Label(
        override val text: String,
        override val icon: Int,
        val secondaryText:String,
        override val isEnable: Boolean = true,
        override val isVisible: Boolean = true,
    ) : SettingsParamType(text,icon,isEnable,isVisible)
}