<script setup>
import {
  VerticalNavLink,
  VerticalNavSectionTitle,
} from '@layouts'
</script>

<template>
    <ul>
        <VerticalNavSectionTitle :item="{ heading: 'Authormanage' }" />
        <VerticalNavLink
            :item="{
                title: '작가',
                to: '/authors',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: 'Writemanage' }" />
        <VerticalNavLink
            :item="{
                title: '글',
                to: '/writings',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '승인여부',
                to: '/approvalAuthors',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: 'Ai' }" />
        <VerticalNavLink
            :item="{
                title: 'CoverDesign',
                to: '/coverDesigns',
            }"
        />
        <VerticalNavLink
            :item="{
                title: 'ContentAnalyzer',
                to: '/contentAnalyzers',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: 'point' }" />
        <VerticalNavLink
            :item="{
                title: 'Point',
                to: '/points',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: 'libraryplatform' }" />
        <VerticalNavLink
            :item="{
                title: '도서정보',
                to: '/libraryInfos',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: '구독관리' }" />
        <VerticalNavLink
            :item="{
                title: '구독자',
                to: '/subscribers',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '구독도서정보',
                to: '/subscribedBooks',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '구독 열람',
                to: '/subscribeLists',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: 'Outside' }" />
    </ul>
</template>
