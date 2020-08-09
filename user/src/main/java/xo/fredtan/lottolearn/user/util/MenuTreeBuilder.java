package xo.fredtan.lottolearn.user.util;

import xo.fredtan.lottolearn.domain.user.response.MenuTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MenuTreeBuilder {
    public static MenuTree build(List<MenuTree> menuTreeList, String rootId) {
        AtomicReference<MenuTree> root = new AtomicReference<>();

        Supplier<Stream<MenuTree>> menuTreeSupplier = menuTreeList::stream;

        menuTreeSupplier.get().forEach(menuTree -> {
            if (rootId.equals(menuTree.getId()))
                root.set(menuTree);

            if (Objects.isNull(menuTree.getChildren()))
                menuTree.setChildren(new ArrayList<>());

            menuTree.getChildren().addAll(menuTreeSupplier.get().filter(m -> menuTree.getId().equals(m.getParentId()))
                    .collect(Collectors.toList()));
        });

        return root.get();
    }
}
