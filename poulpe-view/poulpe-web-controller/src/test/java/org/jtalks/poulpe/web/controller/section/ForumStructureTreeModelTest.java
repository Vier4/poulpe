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
package org.jtalks.poulpe.web.controller.section;

import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.web.controller.zkutils.ZkTreeNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zkoss.zul.TreeNode;

import java.util.ArrayList;

import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

/**
 * @author stanislav bashkirtsev
 */
public class ForumStructureTreeModelTest {
    ForumStructureTreeModel sut;

    @BeforeMethod
    public void setUp() throws Exception {
        sut = new ForumStructureTreeModel(createSectionNode());
        sut.getRoot().add(createSectionNode());
        sut.getRoot().add(createSectionNode());
        sut.getRoot().add(createBranchNode());
        sut.getRoot().getChildAt(1).add(createBranchNode());
        sut.getRoot().getChildAt(1).add(createBranchNode());
        sut.getRoot().getChildAt(1).add(createBranchNode());
    }

    @Test
    public void testPutBranch() throws Exception {
        ForumStructureItem branchToPut = sut.getChild(1, 0).getData();
        ForumStructureItem destinationSection = sut.getChild(0).getData();
        sut.putBranch(branchToPut, destinationSection);
        assertSame(sut.getChild(0, 0).getData(), branchToPut);
        assertNotSame(sut.getChild(1, 0).getData(), branchToPut);
    }

    @Test
    public void putNewBranchShouldCreateNewNode() throws Exception {
        ForumStructureItem branchToPut = new ForumStructureItem(new PoulpeBranch("aa"));
        ForumStructureItem destinationSection = sut.getChild(0).getData();
        sut.putBranch(branchToPut, destinationSection);
        assertSame(sut.getChild(0, 0).getData(), branchToPut);
    }

    private ZkTreeNode<ForumStructureItem> createBranchNode() {
        return new ZkTreeNode<ForumStructureItem>(new ForumStructureItem(null));
    }

    private ZkTreeNode<ForumStructureItem> createSectionNode() {
        return new ZkTreeNode<ForumStructureItem>(new ForumStructureItem(null), new ArrayList<TreeNode<ForumStructureItem>>());
    }
}
