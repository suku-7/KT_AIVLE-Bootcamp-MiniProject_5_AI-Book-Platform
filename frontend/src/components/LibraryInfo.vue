<template>
    <div>
        <Number
            label="BookId"
            v-model="value.bookId"
            :editMode="editMode"
        />
        <String
            label="BookTitle"
            v-model="value.bookTitle"
            :editMode="editMode"
        />
        <Boolean
            label="Bestseller"
            v-model="value.bestseller"
            :editMode="editMode"
        />
        <String
            label="Author"
            v-model="value.author"
            :editMode="editMode"
        />
        <Number
            label="SelectCount"
            v-model="value.selectCount"
            :editMode="editMode"
        />
        <Date
            label="PublishDate"
            v-model="value.publishDate"
            :editMode="editMode"
        />
        <String
            label="Summary"
            v-model="value.summary"
            :editMode="editMode"
        />
        <String
            label="ClassficationTpe"
            v-model="value.classficationTpe"
            :editMode="editMode"
        />
        <String
            label="Bookimage"
            v-model="value.bookimage"
            :editMode="editMode"
        />
        <v-row class="ma-0 pa-0">
            <v-spacer></v-spacer>
            <v-btn width="64px" color="primary" @click="save">
                저장
            </v-btn>
        </v-row>
    </div>
</template>


<script>
import BaseEntity from './base-ui/BaseEntity.vue'

export default {
    name: 'LibraryInfo',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "libraryInfos",
        value: {
        }
    }),
    created(){
    },
    computed:{
    },
    methods: {
    },
}
</script>
