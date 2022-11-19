package com.testframe.autotest.ui.elements.module.action;

import com.testframe.autotest.core.exception.ActionException;
import com.testframe.autotest.ui.elements.module.action.base.BaseAction;
import com.testframe.autotest.ui.elements.module.action.base.ActionI;
import com.testframe.autotest.ui.enums.operate.OperateEnum;
import com.testframe.autotest.ui.meta.OperateData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

/**
 * Description:
 * 单选框/复选框操作
 * @date:2022/10/26 22:05
 * @author: lyf
 */
@Component
public class BoxAction extends BaseAction implements ActionI {

    @Override
    public String actionTypeIdentity() {
        return OperateEnum.BOX_OPERATE.getName();
    }

    /**
     * 选中单选框
     * @param driver
     * @param element
     * @param data
     */
    public static void selected(WebDriver driver, WebElement element, OperateData data) {
        try {
            if (element == null || element.isSelected()) {
                return;
            }
            element.click();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionException("单选框选中失败");
        }
    }

    /**
     * 取消选中单选框
     * @param driver
     * @param element
     * @param data
     */
    public static void notSelected(WebDriver driver, WebElement element, OperateData data) {
        try {
            if (element == null || !element.isSelected()) {
                return;
            }
            element.click();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionException("取消单选框选中失败");
        }
    }


    /**
     * 单选选中下拉列表中的元素
     * @param driver
     * @param element
     * @param data
     */
    public static void selectDropList(WebDriver driver, WebElement element, OperateData data) {
        Boolean selected = true;
        try {
            if (element == null) {
                return;
            }
            Select dropList = new Select(element);
            if (data.getIndexes() != null) {
                dropList.selectByIndex(data.getIndexes().get(0));
                return;
            }
            if (data.getAttr() != null) {
                dropList.selectByValue(data.getAttr());
                return;
            }
            if (data.getValue() != null) {
                dropList.selectByVisibleText(data.getValue());
                return;
            }
            selected = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionException("单选列表选择失败");
        } finally {
            if (selected == false) {
                throw new ActionException("无法选择当前指定的元素，请提供合适的选择方式");
            }
        }
    }


    /**
     * 多选下拉列表选择元素
     * @param driver
     * @param element
     * @param data
     */
    public static void selectMultiDropList(WebDriver driver, WebElement element, OperateData data) {
        // 多选暂时仅支持索引的方式
        // TODO: 2022/10/27 拓展支持多种选择方式
        Boolean selected = true;
        try {
            if (element == null) {
                return;
            }
            Select dropList = new Select(element);
            dropList.deselectAll(); // 取消所有选中态
            if (!dropList.isMultiple()) {
                throw new ActionException("当前不是多选下拉列表，不支持该操作");
            }
            if (data.getIndexes() != null) {
                for (Integer index : data.getIndexes()) {
                    dropList.selectByIndex(index);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionException("多选列表选择失败");
        }
    }

}
