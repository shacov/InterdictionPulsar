package com.azurewrath.interdictionpulsar;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

/**
 * 脉冲阻拦器配置屏幕 - GUI客户端渲染和交互处理
 */
public class InterdictionPulsarScreen extends AbstractContainerScreen<InterdictionPulsarMenu> {

    // 资源常量
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(InterdictionPulsarMod.MOD_ID, "textures/gui/interdiction_pulsar.png");

    // 本地化文本组件
    private static final Component TEXT_TICK_INTERVAL = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.tick_interval");
    private static final Component TEXT_HORIZONTAL_RANGE = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.horizontal_range");
    private static final Component TEXT_VERTICAL_RANGE = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.vertical_range");
    private static final Component TEXT_STRENGTH = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.strength");
    private static final Component TEXT_MODE = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.mode");
    private static final Component TEXT_REDSTONE_MODE = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.redstone_mode");
    private static final Component TEXT_HOSTILE = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.hostile");
    private static final Component TEXT_PASSIVE = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.passive");
    private static final Component TEXT_PLAYER = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.player");
    private static final Component TEXT_CUSTOM_ORIGIN_X = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.custom_origin_x");
    private static final Component TEXT_CUSTOM_ORIGIN_Y = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.custom_origin_y");
    private static final Component TEXT_CUSTOM_ORIGIN_Z = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.custom_origin_z");
    private static final Component TEXT_HORIZONTAL_MODE = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.horizontal_mode");
    private static final Component TEXT_VERTICAL_MODE = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.vertical_mode");

    // 模式文本组件
    private static final Component TEXT_SWITCH = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.switch");
    private static final Component TEXT_ON = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.on");
    private static final Component TEXT_OFF = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.off");
    private static final Component TEXT_MODE_PUSH = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.mode_push");
    private static final Component TEXT_MODE_PULL = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.mode_pull");
    private static final Component TEXT_REDSTONE_IGNORE = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.redstone_ignore");
    private static final Component TEXT_REDSTONE_REQUIRE = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.redstone_require");
    private static final Component TEXT_REDSTONE_STOP = Component.translatable("gui.interdictionpulsar.interdiction_pulsar.redstone_stop");

    // 布局和尺寸常量
    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 286;
    private static final int LABEL_COLUMN = 15;
    private static final int VALUE_COLUMN = 80;
    private static final int SLIDER_COLUMN = 120-8;
    private static final int INPUT_COLUMN = 200;
    private static final int BUTTON_COLUMN = 186;
    private static final int START_Y = 20;
    private static final int ROW_SPACING = 18;
    private static final int SLIDER_WIDTH = 65+8;
    private static final int INPUT_WIDTH = 25;
    private static final int INPUT_WIDTH_LONG = 40;
    private static final int BUTTON_WIDTH = 40;
    private static final int BUTTON_HEIGHT = 16;
    private static final int OFFSET_X = 8;
    private static final int OFFSET_Y = 8;
    private static final int BUTTON_VERTICAL_OFFSET = -2;

    // 输入框实例
    private EditBox tickIntervalInput;
    private EditBox horizontalRangeInput;
    private EditBox verticalRangeInput;
    private EditBox strengthInput;
    private EditBox customOriginXInput;
    private EditBox customOriginYInput;
    private EditBox customOriginZInput;

    // 按钮实例
    private Button modeToggleButton;
    private Button redstoneModeButton;
    private Button affectHostileButton;
    private Button affectPassiveButton;
    private Button affectPlayerButton;
    private Button horizontalModeButton;
    private Button verticalModeButton;

    // 交互状态
    private int sliderDrag = -1;

    public InterdictionPulsarScreen(InterdictionPulsarMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    /**
     * 初始化GUI组件
     */
    @Override
    protected void init() {
        super.init();
        initializeInputBoxes();
        initializeButtons();
    }

    /**
     * 初始化所有输入框组件
     */
    private void initializeInputBoxes() {
        this.tickIntervalInput = createInputBox(0);
        this.horizontalRangeInput = createInputBox(1);
        this.verticalRangeInput = createInputBox(2);
        this.strengthInput = createInputBox(3);

        // 自定义坐标输入框
        this.customOriginXInput = createCustomOriginInputBox(11);
        this.customOriginYInput = createCustomOriginInputBox(12);
        this.customOriginZInput = createCustomOriginInputBox(13);

        // 配置自定义坐标输入框
        configureIntegerInputAllowNegative(customOriginXInput);
        configureIntegerInputAllowNegative(customOriginYInput);
        configureIntegerInputAllowNegative(customOriginZInput);

        // 自定义坐标输入框始终可见
        customOriginXInput.setVisible(true);
        customOriginYInput.setVisible(true);
        customOriginZInput.setVisible(true);

        this.addRenderableWidget(tickIntervalInput);
        this.addRenderableWidget(horizontalRangeInput);
        this.addRenderableWidget(verticalRangeInput);
        this.addRenderableWidget(strengthInput);
        this.addRenderableWidget(customOriginXInput);
        this.addRenderableWidget(customOriginYInput);
        this.addRenderableWidget(customOriginZInput);
    }

    /**
     * 初始化所有按钮组件
     */
    private void initializeButtons() {
        // 工作模式切换按钮
        this.modeToggleButton = Button.builder(TEXT_SWITCH, button -> {  // 修改这里
                    this.menu.clickMenuButton(this.minecraft.player, InterdictionPulsarMenu.BUTTON_MODE_TOGGLE);
                })
                .bounds(
                        this.leftPos + BUTTON_COLUMN + OFFSET_X,
                        this.topPos + START_Y + ROW_SPACING * 4 + BUTTON_VERTICAL_OFFSET + OFFSET_Y,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT
                )
                .build();

        // 红石模式切换按钮
        this.redstoneModeButton = Button.builder(TEXT_SWITCH, button -> {  // 修改这里
                    this.menu.clickMenuButton(this.minecraft.player, InterdictionPulsarMenu.BUTTON_REDSTONE_MODE_CYCLE);
                })
                .bounds(
                        this.leftPos + BUTTON_COLUMN + OFFSET_X,
                        this.topPos + START_Y + ROW_SPACING * 5 + BUTTON_VERTICAL_OFFSET + OFFSET_Y,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT
                )
                .build();

        // 生物类型筛选按钮
        this.affectHostileButton = Button.builder(TEXT_SWITCH, button -> {  // 修改这里
                    this.menu.clickMenuButton(this.minecraft.player, InterdictionPulsarMenu.BUTTON_AFFECT_HOSTILE_TOGGLE);
                })
                .bounds(
                        this.leftPos + BUTTON_COLUMN + OFFSET_X,
                        this.topPos + START_Y + ROW_SPACING * 6 + BUTTON_VERTICAL_OFFSET + OFFSET_Y,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT
                )
                .build();

        this.affectPassiveButton = Button.builder(TEXT_SWITCH, button -> {  // 修改这里
                    this.menu.clickMenuButton(this.minecraft.player, InterdictionPulsarMenu.BUTTON_AFFECT_PASSIVE_TOGGLE);
                })
                .bounds(
                        this.leftPos + BUTTON_COLUMN + OFFSET_X,
                        this.topPos + START_Y + ROW_SPACING * 7 + BUTTON_VERTICAL_OFFSET + OFFSET_Y,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT
                )
                .build();

        this.affectPlayerButton = Button.builder(TEXT_SWITCH, button -> {  // 修改这里
                    this.menu.clickMenuButton(this.minecraft.player, InterdictionPulsarMenu.BUTTON_AFFECT_PLAYER_TOGGLE);
                })
                .bounds(
                        this.leftPos + BUTTON_COLUMN + OFFSET_X,
                        this.topPos + START_Y + ROW_SPACING * 8 + BUTTON_VERTICAL_OFFSET + OFFSET_Y,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT
                )
                .build();

        // 水平模式切换按钮
        this.horizontalModeButton = Button.builder(TEXT_SWITCH, button -> {
                    this.menu.clickMenuButton(this.minecraft.player, InterdictionPulsarMenu.BUTTON_HORIZONTAL_MODE_TOGGLE);
                })
                .bounds(
                        this.leftPos + BUTTON_COLUMN + OFFSET_X,
                        this.topPos + START_Y + ROW_SPACING * 9 + BUTTON_VERTICAL_OFFSET + OFFSET_Y,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT
                )
                .build();

        // 垂直模式切换按钮
        this.verticalModeButton = Button.builder(TEXT_SWITCH, button -> {
                    this.menu.clickMenuButton(this.minecraft.player, InterdictionPulsarMenu.BUTTON_VERTICAL_MODE_TOGGLE);
                })
                .bounds(
                        this.leftPos + BUTTON_COLUMN + OFFSET_X,
                        this.topPos + START_Y + ROW_SPACING * 10 + BUTTON_VERTICAL_OFFSET + OFFSET_Y,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT
                )
                .build();

        this.addRenderableWidget(modeToggleButton);
        this.addRenderableWidget(redstoneModeButton);
        this.addRenderableWidget(affectHostileButton);
        this.addRenderableWidget(affectPassiveButton);
        this.addRenderableWidget(affectPlayerButton);
        this.addRenderableWidget(horizontalModeButton);
        this.addRenderableWidget(verticalModeButton);
    }

    /**
     * 创建指定行的输入框
     */
    private EditBox createInputBox(int row) {
        int yPosition = this.topPos + START_Y + row * ROW_SPACING + OFFSET_Y;
        EditBox input = new EditBox(this.font, this.leftPos + INPUT_COLUMN + OFFSET_X, yPosition, INPUT_WIDTH, 12, Component.literal(""));
        input.setValue("");

        if (row == 3) {
            configureStrengthInput(input);
        } else {
            configureIntegerInput(input);
        }

        return input;
    }

    /**
     * 创建自定义坐标输入框 - 确保位置对齐
     */
    private EditBox createCustomOriginInputBox(int row) {
        int yPosition = this.topPos + START_Y + row * ROW_SPACING + OFFSET_Y;
        int standardInputRightEdge = this.leftPos + INPUT_COLUMN + INPUT_WIDTH;
        int customInputX = standardInputRightEdge - INPUT_WIDTH_LONG + OFFSET_X;

        EditBox input = new EditBox(this.font, customInputX, yPosition, INPUT_WIDTH_LONG, 12, Component.literal(""));
        input.setValue("");
        configureIntegerInputAllowNegative(input);  // 使用允许负数的配置
        return input;
    }

    /**
     * 配置强度输入框（浮点数验证）
     */
    private void configureStrengthInput(EditBox input) {
        input.setFilter(inputText -> {
            if (inputText.isEmpty()) return true;
            if (inputText.equals(".")) return true;
            try {
                Float.parseFloat(inputText);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    /**
     * 配置整数输入框（不允许负数）
     */
    private void configureIntegerInput(EditBox input) {
        input.setFilter(inputText -> {
            if (inputText.isEmpty()) return true;
            try {
                Integer.parseInt(inputText);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    /**
     * 配置整数输入框，允许负数
     */
    private void configureIntegerInputAllowNegative(EditBox input) {
        input.setFilter(inputText -> {
            boolean isValid = inputText.isEmpty() || inputText.matches("-?[0-9]*");

            // 在输入框配置中添加颜色反馈
            // 设置输入框文本颜色（有效=白色，无效=红色）
            if (isValid) {
                input.setTextColor(0xFFFFFF);  // 白色
            } else {
                input.setTextColor(0xFF5555);  // 红色
            }

            return isValid;
        });
    }

    // ================================
    // 渲染方法
    // ================================

    /**
     * 渲染背景层
     */
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // 更详细的blit调用，指定所有参数
        guiGraphics.blit(
                TEXTURE,                    // 纹理
                this.leftPos,               // 目标x位置
                this.topPos,                // 目标y位置
                0,                          // 源x偏移（纹理内的x）
                0,                          // 源y偏移（纹理内的y）
                this.imageWidth,            // 目标宽度
                this.imageHeight,           // 目标高度
                this.imageWidth,            // 纹理宽度
                this.imageHeight            // 纹理高度
        );

        renderSliders(guiGraphics, mouseX, mouseY);
    }

    /**
     * 渲染标签层（文字内容）
     */
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX + OFFSET_X, 6 + OFFSET_Y, 0x404040, false);
        renderBasicParameterRows(guiGraphics);
        renderModeRows(guiGraphics);
        renderEntityTypeRows(guiGraphics);
        renderCustomOriginRows(guiGraphics);
        renderMovementModeRows(guiGraphics);
    }

    /**
     * 渲染基本参数行
     */
    private void renderBasicParameterRows(GuiGraphics guiGraphics) {
        int currentY = START_Y + OFFSET_Y;

        renderParameterRow(guiGraphics, TEXT_TICK_INTERVAL,
                menu.getData().get(InterdictionPulsarMenu.FIELD_TICK_INTERVAL), currentY);
        currentY += ROW_SPACING;

        renderParameterRow(guiGraphics, TEXT_HORIZONTAL_RANGE,
                menu.getData().get(InterdictionPulsarMenu.FIELD_HORIZONTAL_RANGE), currentY);
        currentY += ROW_SPACING;

        renderParameterRow(guiGraphics, TEXT_VERTICAL_RANGE,
                menu.getData().get(InterdictionPulsarMenu.FIELD_VERTICAL_RANGE), currentY);
        currentY += ROW_SPACING;

        // 推力强度行（特殊格式：浮点数显示）
        guiGraphics.drawString(this.font, TEXT_STRENGTH, LABEL_COLUMN + OFFSET_X, currentY, 0x404040, false);
        String strengthValue = String.format("%.1f", menu.getData().get(InterdictionPulsarMenu.FIELD_STRENGTH) / 10.0f);
        guiGraphics.drawString(this.font, strengthValue, VALUE_COLUMN + OFFSET_X, currentY, 0xFFFFFF, false);
    }

    /**
     * 渲染单个参数行
     */
    private void renderParameterRow(GuiGraphics guiGraphics, Component label, int value, int yPosition) {
        guiGraphics.drawString(this.font, label, LABEL_COLUMN + OFFSET_X, yPosition, 0x404040, false);
        guiGraphics.drawString(this.font, String.valueOf(value), VALUE_COLUMN + OFFSET_X, yPosition, 0xFFFFFF, false);
    }

    /**
     * 渲染模式配置行
     */
    private void renderModeRows(GuiGraphics guiGraphics) {
        int currentY = START_Y + ROW_SPACING * 4 + OFFSET_Y;

        Component modeText = menu.getData().get(InterdictionPulsarMenu.FIELD_MODE) == 0 ? TEXT_MODE_PUSH : TEXT_MODE_PULL;
        renderModeRow(guiGraphics, TEXT_MODE, modeText.getString(), currentY);
        currentY += ROW_SPACING;

        Component redstoneModeText = getRedstoneModeText(menu.getData().get(InterdictionPulsarMenu.FIELD_REDSTONE_MODE));
        renderModeRow(guiGraphics, TEXT_REDSTONE_MODE, redstoneModeText.getString(), currentY);
    }

    /**
     * 渲染单个模式行
     */
    private void renderModeRow(GuiGraphics guiGraphics, Component label, String valueText, int yPosition) {
        guiGraphics.drawString(this.font, label, LABEL_COLUMN + OFFSET_X, yPosition, 0x404040, false);
        guiGraphics.drawString(this.font, valueText, VALUE_COLUMN + OFFSET_X, yPosition, 0xFFFFFF, false);
    }

    /**
     * 获取红石模式的显示文本
     */
    private Component getRedstoneModeText(int redstoneMode) {
        return switch (redstoneMode) {
            case 0 -> TEXT_REDSTONE_IGNORE;
            case 1 -> TEXT_REDSTONE_REQUIRE;
            case 2 -> TEXT_REDSTONE_STOP;
            default -> Component.literal("Unknown");
        };
    }

    /**
     * 渲染生物类型筛选行
     */
    private void renderEntityTypeRows(GuiGraphics guiGraphics) {
        int currentY = START_Y + ROW_SPACING * 6 + OFFSET_Y;

        Component hostileStatusText = menu.getData().get(InterdictionPulsarMenu.FIELD_AFFECT_HOSTILE) == 1 ? TEXT_ON : TEXT_OFF;
        renderModeRow(guiGraphics, TEXT_HOSTILE, hostileStatusText.getString(), currentY);
        currentY += ROW_SPACING;

        Component passiveStatusText = menu.getData().get(InterdictionPulsarMenu.FIELD_AFFECT_PASSIVE) == 1 ? TEXT_ON : TEXT_OFF;
        renderModeRow(guiGraphics, TEXT_PASSIVE, passiveStatusText.getString(), currentY);
        currentY += ROW_SPACING;

        Component playerStatusText = menu.getData().get(InterdictionPulsarMenu.FIELD_AFFECT_PLAYER) == 1 ? TEXT_ON : TEXT_OFF;
        renderModeRow(guiGraphics, TEXT_PLAYER, playerStatusText.getString(), currentY);
    }

    /**
     * 渲染自定义坐标配置行
     */
    private void renderCustomOriginRows(GuiGraphics guiGraphics) {
        int currentY = START_Y + ROW_SPACING * 11 + OFFSET_Y;  // 从9改为11改为13
        renderCustomOriginCoordinateRows(guiGraphics, currentY);
    }

    /**
     * 渲染自定义坐标的具体坐标行
     */
    private void renderCustomOriginCoordinateRows(GuiGraphics guiGraphics, int startY) {
        int currentY = startY;

        // X坐标
        guiGraphics.drawString(this.font, TEXT_CUSTOM_ORIGIN_X, LABEL_COLUMN + OFFSET_X, currentY, 0x404040, false);
        guiGraphics.drawString(this.font, String.valueOf(menu.getData().get(InterdictionPulsarMenu.FIELD_CUSTOM_ORIGIN_X)), VALUE_COLUMN + OFFSET_X, currentY, 0xFFFFFF, false);
        currentY += ROW_SPACING;

        // Y坐标
        guiGraphics.drawString(this.font, TEXT_CUSTOM_ORIGIN_Y, LABEL_COLUMN + OFFSET_X, currentY, 0x404040, false);
        guiGraphics.drawString(this.font, String.valueOf(menu.getData().get(InterdictionPulsarMenu.FIELD_CUSTOM_ORIGIN_Y)), VALUE_COLUMN + OFFSET_X, currentY, 0xFFFFFF, false);
        currentY += ROW_SPACING;

        // Z坐标
        guiGraphics.drawString(this.font, TEXT_CUSTOM_ORIGIN_Z, LABEL_COLUMN + OFFSET_X, currentY, 0x404040, false);
        guiGraphics.drawString(this.font, String.valueOf(menu.getData().get(InterdictionPulsarMenu.FIELD_CUSTOM_ORIGIN_Z)), VALUE_COLUMN + OFFSET_X, currentY, 0xFFFFFF, false);
    }

    /**
     * 渲染移动模式配置行
     */
    private void renderMovementModeRows(GuiGraphics guiGraphics) {
        int currentY = START_Y + ROW_SPACING * 9 + OFFSET_Y;

        Component horizontalModeText = menu.getData().get(InterdictionPulsarMenu.FIELD_HORIZONTAL_MODE) == 1 ? TEXT_ON : TEXT_OFF;
        renderModeRow(guiGraphics, TEXT_HORIZONTAL_MODE, horizontalModeText.getString(), currentY);
        currentY += ROW_SPACING;

        Component verticalModeText = menu.getData().get(InterdictionPulsarMenu.FIELD_VERTICAL_MODE) == 1 ? TEXT_ON : TEXT_OFF;
        renderModeRow(guiGraphics, TEXT_VERTICAL_MODE, verticalModeText.getString(), currentY);
    }

    /**
     * 当容器数据改变时调用
     */
    @Override
    public void containerTick() {
        super.containerTick();
        // 自定义坐标输入框始终可见，无需更新可见性
    }

    // ================================
    // 滑动条渲染和交互
    // ================================

    /**
     * 渲染所有滑动条
     */
    private void renderSliders(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int[] sliderValues = {
                menu.getData().get(InterdictionPulsarMenu.FIELD_TICK_INTERVAL),
                menu.getData().get(InterdictionPulsarMenu.FIELD_HORIZONTAL_RANGE),
                menu.getData().get(InterdictionPulsarMenu.FIELD_VERTICAL_RANGE),
                menu.getData().get(InterdictionPulsarMenu.FIELD_STRENGTH)
        };

        int[] maxValues = {100, 64, 64, 50};

        for (int i = 0; i < 4; i++) {
            renderSlider(guiGraphics, mouseX, mouseY, i, sliderValues[i], maxValues[i]);
        }
    }

    /**
     * 渲染单个滑动条
     */
    private void renderSlider(GuiGraphics guiGraphics, int mouseX, int mouseY, int sliderIndex, int currentValue, int maxValue) {
        int sliderX = this.leftPos + SLIDER_COLUMN + OFFSET_X;
        int sliderY = this.topPos + START_Y + sliderIndex * ROW_SPACING + 2 + OFFSET_Y;
        int sliderWidth = SLIDER_WIDTH;

        // 绘制滑动条背景轨道
        guiGraphics.fill(sliderX, sliderY, sliderX + sliderWidth, sliderY + 5, 0xFF555555);

        // 绘制进度填充
        float progress = (float) currentValue / maxValue;
        int fillWidth = (int)(progress * sliderWidth);
        if (fillWidth > 0) {
            guiGraphics.fill(sliderX, sliderY, sliderX + fillWidth, sliderY + 5, 0xFF888888);
        }

        // 计算滑块位置
        int thumbX = sliderX + (int)(progress * (sliderWidth - 4));
        boolean isHovered = isMouseOverSlider(mouseX, mouseY, sliderIndex);
        boolean isDragging = (sliderDrag == sliderIndex);

        // 绘制滑块（根据状态改变颜色）
        int thumbColor = isDragging ? 0xFFFFA500 : (isHovered ? 0xFF00FF00 : 0xFFFFFFFF);
        guiGraphics.fill(thumbX, sliderY - 1, thumbX + 4, sliderY + 6, thumbColor);
    }

    /**
     * 检查鼠标是否在指定滑动条上方
     */
    private boolean isMouseOverSlider(int mouseX, int mouseY, int sliderIndex) {
        int sliderX = this.leftPos + SLIDER_COLUMN + OFFSET_X;
        int sliderY = this.topPos + START_Y + sliderIndex * ROW_SPACING + OFFSET_Y;
        int sliderWidth = SLIDER_WIDTH;
        int sliderHeight = 8;

        return mouseX >= sliderX && mouseX < sliderX + sliderWidth &&
                mouseY >= sliderY && mouseY < sliderY + sliderHeight;
    }

    // ================================
    // 鼠标交互处理
    // ================================

    /**
     * 处理鼠标点击事件
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查滑动条点击（开始拖拽）
        for (int i = 0; i < 4; i++) {
            if (isMouseOverSlider((int)mouseX, (int)mouseY, i)) {
                this.sliderDrag = i;
                playClickSound(1.0F);
                return true;
            }
        }

        // 移除复选框点击处理，因为现在使用标准按钮

        // 如果点击了输入框外的区域，提交所有输入框的内容
        if (!isMouseOverAnyInput((int)mouseX, (int)mouseY)) {
            submitAllInputBoxes();
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * 播放点击音效
     */
    private void playClickSound(float volume) {
        Objects.requireNonNull(this.minecraft).getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, volume));
    }

    /**
     * 检查鼠标是否在任意输入框上方
     */
    private boolean isMouseOverAnyInput(int mouseX, int mouseY) {
        return tickIntervalInput.isMouseOver(mouseX, mouseY) ||
                horizontalRangeInput.isMouseOver(mouseX, mouseY) ||
                verticalRangeInput.isMouseOver(mouseX, mouseY) ||
                strengthInput.isMouseOver(mouseX, mouseY) ||
                customOriginXInput.isMouseOver(mouseX, mouseY) ||
                customOriginYInput.isMouseOver(mouseX, mouseY) ||
                customOriginZInput.isMouseOver(mouseX, mouseY);
    }

    /**
     * 提交所有输入框的内容到服务端
     */
    private void submitAllInputBoxes() {
        updateInputBoxIfFocused(tickIntervalInput, InterdictionPulsarMenu.FIELD_TICK_INTERVAL, 1, 100);
        updateInputBoxIfFocused(horizontalRangeInput, InterdictionPulsarMenu.FIELD_HORIZONTAL_RANGE, 1, 64);
        updateInputBoxIfFocused(verticalRangeInput, InterdictionPulsarMenu.FIELD_VERTICAL_RANGE, 1, 16);
        updateStrengthInputIfFocused();
        updateCustomOriginInputIfFocused(customOriginXInput, InterdictionPulsarMenu.FIELD_CUSTOM_ORIGIN_X);
        updateCustomOriginInputIfFocused(customOriginYInput, InterdictionPulsarMenu.FIELD_CUSTOM_ORIGIN_Y);
        updateCustomOriginInputIfFocused(customOriginZInput, InterdictionPulsarMenu.FIELD_CUSTOM_ORIGIN_Z);
    }

    /**
     * 如果输入框有焦点则更新其值
     */
    private void updateInputBoxIfFocused(EditBox input, int fieldId, int min, int max) {
        if (input.isFocused()) {
            updateFromInputBox(input, fieldId, min, max);
            input.setFocused(false);
        }
    }

    /**
     * 如果强度输入框有焦点则更新其值
     */
    private void updateStrengthInputIfFocused() {
        if (strengthInput.isFocused()) {
            updateStrengthFromInput();
            strengthInput.setFocused(false);
        }
    }

    /**
     * 如果自定义坐标输入框有焦点则更新其值
     */
    private void updateCustomOriginInputIfFocused(EditBox input, int fieldId) {
        if (input.isFocused()) {
            updateCustomOriginFromInput(input, fieldId);
            input.setFocused(false);
        }
    }

    /**
     * 处理鼠标释放事件
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.sliderDrag != -1) {
            this.sliderDrag = -1;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * 处理鼠标拖拽事件
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.sliderDrag != -1) {
            int sliderX = this.leftPos + SLIDER_COLUMN + OFFSET_X;
            int sliderWidth = SLIDER_WIDTH;

            // 修正：使用相对于滑动条起始位置的坐标
            double relativeX = mouseX - sliderX;
            double progress = Math.max(0, Math.min(1, relativeX / sliderWidth));

            int newValue = calculateSliderValue(this.sliderDrag, progress);
            setSliderValue(this.sliderDrag, newValue);

            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    // ================================
    // 鼠标滚轮支持
    // ================================

    /**
     * 处理鼠标滚轮事件 - 修复方法签名
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (int i = 0; i < 4; i++) {
            if (isMouseOverSlider((int) mouseX, (int) mouseY, i)) {
                int currentValue = getCurrentSliderValue(i);
                int step = getSliderStep(i);
                // 使用 scrollY 而不是 delta
                int newValue = currentValue + (scrollY > 0 ? step : -step);

                int[] minValues = {1, 1, 1, 1};
                int[] maxValues = {100, 64, 64, 50};

                newValue = Math.max(minValues[i], Math.min(maxValues[i], newValue));
                setSliderValue(i, newValue);
                playClickSound(0.5F);
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    /**
     * 获取滑动条的当前值
     */
    private int getCurrentSliderValue(int sliderIndex) {
        return switch (sliderIndex) {
            case 0 -> menu.getData().get(InterdictionPulsarMenu.FIELD_TICK_INTERVAL);
            case 1 -> menu.getData().get(InterdictionPulsarMenu.FIELD_HORIZONTAL_RANGE);
            case 2 -> menu.getData().get(InterdictionPulsarMenu.FIELD_VERTICAL_RANGE);
            case 3 -> menu.getData().get(InterdictionPulsarMenu.FIELD_STRENGTH);
            default -> 0;
        };
    }

    /**
     * 获取滑动条的调整步长
     */
    private int getSliderStep(int sliderIndex) {
        return switch (sliderIndex) {
            case 0, 1, 2, 3 -> 1;
            default -> 1;
        };
    }

    // ================================
    // 滑动条值计算和设置
    // ================================

    /**
     * 根据进度计算滑动条的值
     */
    private int calculateSliderValue(int sliderIndex, double progress) {
        int[] minValues = {1, 1, 1, 1};
        int[] maxValues = {100, 64, 64, 50};

        return minValues[sliderIndex] + (int)(progress * (maxValues[sliderIndex] - minValues[sliderIndex]));
    }

    /**
     * 设置滑动条的值并发送到服务端 - 修复网络发送
     */
    private void setSliderValue(int sliderIndex, int value) {
        int fieldId = switch (sliderIndex) {
            case 0 -> InterdictionPulsarMenu.FIELD_TICK_INTERVAL;
            case 1 -> InterdictionPulsarMenu.FIELD_HORIZONTAL_RANGE;
            case 2 -> InterdictionPulsarMenu.FIELD_VERTICAL_RANGE;
            case 3 -> InterdictionPulsarMenu.FIELD_STRENGTH;
            default -> -1;
        };

        if (fieldId != -1) {
            // 使用新的网络发送方式
            if (minecraft != null && minecraft.player != null) {
                PulsarConfigPacket packet = new PulsarConfigPacket(menu.getBlockPos(), fieldId, value);
                minecraft.player.connection.send(packet);
            }
        }
    }

    // ================================
    // 键盘输入处理
    // ================================

    /**
     * 处理键盘按键事件
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 处理输入框的Enter键提交
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            handleEnterKeyPress();
            return true;
        }

        // 处理ESC键 - 关闭GUI前提交所有输入框
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            submitAllInputBoxes();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * 处理Enter键按下事件
     */
    private void handleEnterKeyPress() {
        if (tickIntervalInput.isFocused()) {
            updateFromInputBox(tickIntervalInput, InterdictionPulsarMenu.FIELD_TICK_INTERVAL, 1, 100);
            tickIntervalInput.setFocused(false);
        } else if (horizontalRangeInput.isFocused()) {
            updateFromInputBox(horizontalRangeInput, InterdictionPulsarMenu.FIELD_HORIZONTAL_RANGE, 1, 64);
            horizontalRangeInput.setFocused(false);
        } else if (verticalRangeInput.isFocused()) {
            updateFromInputBox(verticalRangeInput, InterdictionPulsarMenu.FIELD_VERTICAL_RANGE, 1, 64);
            verticalRangeInput.setFocused(false);
        } else if (strengthInput.isFocused()) {
            updateStrengthFromInput();
            strengthInput.setFocused(false);
        } else if (customOriginXInput.isFocused()) {
            updateCustomOriginFromInput(customOriginXInput, InterdictionPulsarMenu.FIELD_CUSTOM_ORIGIN_X);
            customOriginXInput.setFocused(false);
        } else if (customOriginYInput.isFocused()) {
            updateCustomOriginFromInput(customOriginYInput, InterdictionPulsarMenu.FIELD_CUSTOM_ORIGIN_Y);
            customOriginYInput.setFocused(false);
        } else if (customOriginZInput.isFocused()) {
            updateCustomOriginFromInput(customOriginZInput, InterdictionPulsarMenu.FIELD_CUSTOM_ORIGIN_Z);
            customOriginZInput.setFocused(false);
        }
    }

    // ================================
    // 输入框数据处理
    // ================================

    /**
     * 从输入框更新整数字段值 - 修复网络发送
     */
    private void updateFromInputBox(EditBox input, int fieldId, int min, int max) {
        try {
            int value = Integer.parseInt(input.getValue());
            if (value < min) {
                value = min;
            } else if (value > max) {
                value = max;
            }
            // 使用新的网络发送方式
            if (minecraft != null && minecraft.player != null) {
                PulsarConfigPacket packet = new PulsarConfigPacket(menu.getBlockPos(), fieldId, value);
                minecraft.player.connection.send(packet);
            }
            input.setValue("");
        } catch (NumberFormatException e) {
            input.setValue("");
        }
    }

    /**
     * 从强度输入框更新浮点数值 - 修复网络发送
     */
    private void updateStrengthFromInput() {
        try {
            float value = Float.parseFloat(strengthInput.getValue());
            if (value < 0.1f) {
                value = 0.1f;
            } else if (value > 5.0f) {
                value = 5.0f;
            }
            int intValue = (int)(value * 10);
            // 使用新的网络发送方式
            if (minecraft != null && minecraft.player != null) {
                PulsarConfigPacket packet = new PulsarConfigPacket(menu.getBlockPos(), InterdictionPulsarMenu.FIELD_STRENGTH, intValue);
                minecraft.player.connection.send(packet);
            }
            strengthInput.setValue("");
        } catch (NumberFormatException e) {
            strengthInput.setValue("");
        }
    }

    /**
     * 从自定义坐标输入框更新坐标值
     */
    private void updateCustomOriginFromInput(EditBox input, int fieldId) {
        try {
            int value = Integer.parseInt(input.getValue());
            // 对于坐标值，使用Minecraft世界的合理范围限制
            int min = -30000000;
            int max = 30000000;
            if (value < min) {
                value = min;
            } else if (value > max) {
                value = max;
            }
            // 使用新的网络发送方式
            if (minecraft != null && minecraft.player != null) {
                PulsarConfigPacket packet = new PulsarConfigPacket(menu.getBlockPos(), fieldId, value);
                minecraft.player.connection.send(packet);
            }
            input.setValue("");
        } catch (NumberFormatException e) {
            input.setValue("");
        }
    }

    // ================================
    // 生命周期管理
    // ================================

    /**
     * 处理GUI关闭事件
     */
    @Override
    public void onClose() {
        submitAllInputBoxes();
        super.onClose();
    }
}