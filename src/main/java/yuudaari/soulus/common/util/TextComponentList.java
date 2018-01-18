package yuudaari.soulus.common.util;

import java.util.Arrays;
import java.util.Iterator;
import com.google.common.collect.Iterators;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;

public class TextComponentList extends TextComponentBase {

	private final ITextComponent[] children;

	public TextComponentList (Object... args) {
		children = new ITextComponent[args.length];
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof ITextComponent)
				children[i] = (ITextComponent) args[i];

			else if (args[i] instanceof String)
				children[i] = new TextComponentString((String) args[i]);

			children[i].getStyle().setParentStyle(this.getStyle());
		}
	}

	@Override
	public String getUnformattedComponentText () {
		StringBuilder stringbuilder = new StringBuilder();

		for (ITextComponent itextcomponent : this.children) {
			stringbuilder.append(itextcomponent.getUnformattedComponentText());
		}

		return stringbuilder.toString();
	}

	@Override
	public TextComponentList createCopy () {
		Object[] cloneChildren = new Object[this.children.length];

		for (int i = 0; i < this.children.length; ++i) {
			cloneChildren[i] = this.children[i].createCopy();
		}

		TextComponentList copy = new TextComponentList(cloneChildren);
		copy.setStyle(this.getStyle().createShallowCopy());

		for (ITextComponent itextcomponent : this.getSiblings()) {
			copy.appendSibling(itextcomponent.createCopy());
		}

		return copy;
	}

	@Override
	public boolean equals (Object check) {
		if (this == check) return true;
		if (!(check instanceof TextComponentList)) return false;

		TextComponentList compare = (TextComponentList) check;
		return Arrays.equals(this.children, compare.children) && super.equals(check);
	}

	@Override
	public ITextComponent setStyle (Style style) {
		super.setStyle(style);

		for (ITextComponent child : this.children) {
			child.getStyle().setParentStyle(style);
		}

		return this;
	}

	@Override
	public Iterator<ITextComponent> iterator () {
		return Iterators
			.<ITextComponent>concat(createDeepCopyIterator(Arrays
				.asList(this.children)), createDeepCopyIterator(this.siblings));
	}

	@Override
	public String toString () {
		return "ListComponent{args=" + Arrays
			.toString(this.children) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
	}

	public int hashCode () {
		int i = super.hashCode();
		i = 31 * i + Arrays.hashCode(this.children);
		return i;
	}
}
