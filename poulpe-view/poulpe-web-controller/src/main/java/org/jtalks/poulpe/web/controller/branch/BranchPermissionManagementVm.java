/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.web.controller.branch;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.Validate;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.poulpe.model.dto.PermissionForEntity;
import org.jtalks.poulpe.model.dto.branches.BranchPermissions;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.service.BranchService;
import org.jtalks.poulpe.web.controller.SelectedEntity;
import org.jtalks.poulpe.web.controller.WindowManager;
import org.jtalks.poulpe.web.controller.zkmacro.PermissionManagementBlock;
import org.jtalks.poulpe.web.controller.zkmacro.PermissionRow;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;

import com.google.common.collect.Lists;

/**
 * A View Model for page that allows user to specify what actions can be done with the specific branch and what user
 * groups can do them.
 * 
 * @author stanislav bashkirtsev
 * @author Vyacheslav Zhivaev
 */
public class BranchPermissionManagementVm {

    public static final String MANAGE_GROUPS_DIALOG_ZUL = "/sections/EditGroupsForBranchPermission.zul";

    // Injected
    private final WindowManager windowManager;
    private final BranchService branchService;
    private final SelectedEntity<Object> selectedEntity;

    private final PoulpeBranch branch;
    private final List<PermissionManagementBlock> blocks = Lists.newArrayList();

    /**
     * Constructs the VM with given dependencies.
     * 
     * @param windowManager the window manager instance
     * @param branchService branch service
     * @param selectedEntity the selectedEntity with PoulpeBranch to edit
     */
    public BranchPermissionManagementVm(@Nonnull WindowManager windowManager, @Nonnull BranchService branchService,
            @Nonnull SelectedEntity<Object> selectedEntity) {
        this.windowManager = windowManager;
        this.branchService = branchService;
        this.selectedEntity = selectedEntity;
        this.branch = (PoulpeBranch) selectedEntity.getEntity();
        initDataForView();
    }

    /**
     * Command for showing dialog with editing groups list for current permission.
     * 
     * @param params the URL-style formatted parameters from zul
     */
    @Command
    public void showGroupsDialog(@BindingParam("permission") JtalksPermission permission,
            @BindingParam("mode") String mode) {
        boolean allowed = "allow".equalsIgnoreCase(mode);
        Validate.isTrue(allowed || "restrict".equalsIgnoreCase(mode),
                "Illegal format of parameter 'mode', it can be only 'allow' or 'restrict'");

        PermissionForEntity permissionForEntity = new PermissionForEntity(branch, allowed, permission);

        selectedEntity.setEntity(permissionForEntity);
        windowManager.open(MANAGE_GROUPS_DIALOG_ZUL);
    }

    /**
     * Initializes the data for view.
     */
    private void initDataForView() {
        BranchPermissions groupAccessList = branchService.getGroupAccessListFor(branch);
        for (BranchPermission permission : groupAccessList.getPermissions()) {
            PermissionRow allowRow = PermissionRow.newAllowRow(groupAccessList.getAllowed(permission));
            PermissionRow restrictRow = PermissionRow.newRestrictRow(groupAccessList.getRestricted(permission));
            blocks.add(new PermissionManagementBlock(permission, allowRow, restrictRow));
        }
    }

    /**
     * Gets blocks which represents state of each permission.
     * 
     * @return all blocks
     */
    @SuppressWarnings("unchecked")
    public List<PermissionManagementBlock> getBlocks() {
        return ListUtils.unmodifiableList(blocks);
    }

    /**
     * Gets current branch for edit.
     * 
     * @return the branch to edit
     */
    public PoulpeBranch getBranch() {
        return branch;
    }

}
