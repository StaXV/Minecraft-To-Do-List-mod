package com.jg.memo.client.screen.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;

/**
 * A vanilla slider whose label and applied value are provided by callbacks.
 */
public class SettingsSlider extends AbstractSliderButton {
	private final String labelKey;
	private final DoubleUnaryOperator toDisplay;
	private final Consumer<Double> applier;

	public SettingsSlider(int x, int y, int width, int height, String labelKey,
			double value, DoubleUnaryOperator toDisplay, Consumer<Double> applier) {
		super(x, y, width, height, Component.literal(""), value);
		this.labelKey = labelKey;
		this.toDisplay = toDisplay;
		this.applier = applier;
		updateMessage();
	}

	@Override
	protected void updateMessage() {
		setMessage(Component.translatable(labelKey, displayValue(value)));
	}

	private Object displayValue(double v) {
		double d = toDisplay.applyAsDouble(v);
		if (d == Math.rint(d)) {
			return String.valueOf((long) d);
		}
		return String.valueOf(d);
	}

	@Override
	protected void applyValue() {
		applier.accept(value);
	}

	/** Sets the slider position without triggering the applier. */
	public void setSilently(double value) {
		setValue(value);
	}
}
