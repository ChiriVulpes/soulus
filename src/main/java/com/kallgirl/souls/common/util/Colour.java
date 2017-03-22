package com.kallgirl.souls.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Colour {
	private int colour;

	public Colour (byte r, byte g, byte b) {
		set(r, g, b);
	}

	public Colour (int r, int g, int b) {
		set((byte) r, (byte) g, (byte) b);
	}

	public Colour (int hexCode) {
		set(hexCode);
	}

	public Colour set (byte r, byte g, byte b) {
		this.colour = r;
		this.colour = (this.colour << 8) + g;
		this.colour = (this.colour << 8) + b;
		return this;
	}

	public Colour set (int hexCode) {
		this.colour = hexCode;
		return this;
	}

	public int get () {
		return this.colour;
	}

	public int getRed () {
		return (this.colour >> 16) & 0xFF;
	}

	public int getGreen () {
		return (this.colour >> 8) & 0xFF;
	}

	public int getBlue () {
		return this.colour & 0xFF;
	}

	public static Colour mix (Colour... colours) {
		int red = 0, blue = 0, green = 0;
		for (Colour colour : colours) {
			red += colour.getRed();
			blue += colour.getBlue();
			green += colour.getGreen();
		}
		return new Colour(red / colours.length, green / colours.length, blue / colours.length);
	}

	public static Colour mix (Colour firstColour, Colour secondColour, float percentage) {
		float red, blue, green;

		red = firstColour.getRed() * (1F - percentage);
		green = firstColour.getGreen() * (1F - percentage);
		blue = firstColour.getBlue() * (1F - percentage);

		red += secondColour.getRed() * percentage;
		green += secondColour.getGreen() * percentage;
		blue += secondColour.getBlue() * percentage;

		return new Colour((byte) Math.floor(red), (byte) Math.floor(green), (byte) Math.floor(blue));
	}

	public Colour mixWith (Colour... colours) {
		List<Colour> colourList = Arrays.asList(colours);
		colourList.add(this);
		return Colour.mix((Colour[]) colourList.toArray());
	}

	public Colour mixWith (Colour secondColour, float percentage) {
		return Colour.mix(this, secondColour, percentage);
	}

	public static Colour mix (int... hexColours) {
		List<Colour> colours = new ArrayList<>();
		for (int hexColour : hexColours) colours.add(new Colour(hexColour));
		return mix((Colour[]) colours.toArray());
	}

	public static Colour mix (int firstHexColour, int secondHexColour, float percentage) {
		return mix(new Colour(firstHexColour), new Colour(secondHexColour), percentage);
	}

	public Colour mixWith (int... hexColours) {
		List<Colour> colours = new ArrayList<>();
		colours.add(this);
		for (int hexColour : hexColours) colours.add(new Colour(hexColour));
		return mix((Colour[]) colours.toArray());
	}

	public Colour mixWith (int secondHexColour, float percentage) {
		return mix(this, new Colour(secondHexColour), percentage);
	}
}
