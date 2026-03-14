package org.shuangfa114.test.createtrackbuilder;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.MenuEntry;
import org.shuangfa114.test.createtrackbuilder.content.block.builder.BuilderMenu;
import org.shuangfa114.test.createtrackbuilder.content.block.builder.BuilderScreen;

public class ModMenuTypes {
    public static final CreateRegistrate REGISTRATE = CreateTrackBuilder.registrate();
    public static final MenuEntry<BuilderMenu> BUILDER = REGISTRATE.menu("builder", BuilderMenu::new, () -> BuilderScreen::new).register();

    public static void register() {

    }
}
