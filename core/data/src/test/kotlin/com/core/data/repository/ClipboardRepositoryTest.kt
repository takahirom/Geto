package com.core.data.repository

import com.core.testing.wrapper.TestBuildVersionWrapper
import com.core.testing.wrapper.TestClipboardManagerWrapper
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class ClipboardRepositoryTest {
    private lateinit var buildVersionWrapper: TestBuildVersionWrapper

    private lateinit var clipboardManagerWrapper: TestClipboardManagerWrapper

    private lateinit var subject: DefaultClipboardRepository

    @Before
    fun setUp() {
        buildVersionWrapper = TestBuildVersionWrapper()

        clipboardManagerWrapper = TestClipboardManagerWrapper()

        subject = DefaultClipboardRepository(
            clipboardManagerWrapper = clipboardManagerWrapper,
            buildVersionWrapper = buildVersionWrapper
        )
    }

    @Test
    fun resultIsNull_whenSetPrimaryClipOnApi32AndHigher() {
        buildVersionWrapper.setApi32(true)

        val result = subject.setPrimaryClip(label = "label", text = "text")

        assertNull(result)
    }

    @Test
    fun resultIsNotNull_whenSetPrimaryClipOnApi32AndLower() {
        buildVersionWrapper.setApi32(false)

        val result = subject.setPrimaryClip(label = "label", text = "text")

        assertNotNull(result)
    }

}