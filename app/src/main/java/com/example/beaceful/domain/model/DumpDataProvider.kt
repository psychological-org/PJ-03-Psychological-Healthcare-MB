package com.example.beaceful.domain.model

import java.time.LocalDateTime

object DumpDataProvider {

    /* ---------- ROLES ---------- */
    val roles = listOf(
        Role(1, RoleType.ADMIN),
        Role(2, RoleType.DOCTOR),
        Role(3, RoleType.PATIENT)
    )

    /* ---------- USERS ---------- */
    val listUser: List<User> = listOf(
        // 1. Admin
        User(
            id = 1,
            fullName = "Alice Nguyễn",
            roleId = 1,                              // ADMIN
            biography = "Quản trị viên hệ thống.",
            yearOfBirth = 1990,
            avatarUrl = "https://cdn.example.com/avatars/alice.jpg",
            backgroundUrl = "https://cdn.example.com/bgs/admin_bg.jpg",
            email = "alice.admin@example.com",
            phone = "+84 912‑345‑678",
            password = "hashed_pw_admin"
        ),

        // 2. Doctor #1
        User(
            id = 2,
            fullName = "Đặng Xuân Lan",
            roleId = 2,                              // DOCTOR
            biography = "Tôi thực sự vui mừng được cung cấp cho bạn sự hỗ trợ của tôi. Tôi hy vọng rằng bạn sẽ dành chút thời gian đọc qua hồ sơ của tôi để có thể cảm nhận được cách làm việc của tôi. Tôi rất sẵn lòng thảo luận với bạn về cách tôi có thể giúp bạn tốt nhất trong lần gặp đầu tiên của chúng ta. Tôi đã được đào tạo ở trình độ tiến sĩ về Tâm lý Tư vấn và có thể sử dụng nhiều cách tiếp cận để phù hợp với bạn nhất có thể. Nếu bạn quyết định làm việc với tôi, tôi nghĩ bạn sẽ thấy tôi là một người ấm áp, không phán xét và đồng cảm. Tôi sẽ làm việc theo tốc độ của bạn và giúp tận dụng tốt nhất khoảng thời gian chúng ta bên nhau. Tôi luôn ưu tiên lắng nghe, thấu hiểu và đưa ra liệu pháp hữu ích cho bạn. Nói chung, tôi sẽ hỗ trợ bạn hướng tới kết quả mà bạn muốn.",
            yearOfBirth = 1981,
            yearOfExperience = 12,
            avatarUrl = "https://s3-alpha-sig.figma.com/img/f953/bbe1/5370f69b9375f4ea05a5821cf8c9a75f?Expires=1745798400&Key-Pair-Id=APKAQ4GOSFWCW27IBOMQ&Signature=d7b5Poc46pQqjfSxjLzsEip4njMDwKfr-14Y4Yt~TwNcKaGivFcG~-HokJZhm93dfQuqknzjkaOpx8jEptyZAsYCwJwQjpABW0ynF3yKrVESWfeLXqVJsPLM8nMqiW8K3nHYmgbykIEuJkKEglmYsyXBsc-Lubq9paBzdnDo2Lhj7xWQ8TsIyy4UZh9dbtnomTeW5b5Z0uoHsr3wL-3CnfkkORvZDvOBqgD4b9uCOkH3OldPCGuT23qQskE6Iakd58bMw8C21LtoUzmUkj3qHTc0ETsDrohBDiLFF7AWrzx03JlZI6KdendUU5g7lMl2oFDKfzIoyFqYYAb4UifpAw__",
            backgroundUrl = "https://cdn.example.com/bgs/clinic.jpg",
            email = "binh.tran@healthapp.com",
            phone = "+84 905‑222‑111",
            password = "hashed_pw_binh",
            headline = "Đặng Xuân Lan/ 28 tuổi/ Bác sĩ tâm lý Bạn sẽ tìm thấy niềm vui khi giúp đỡ người khác bằng cả tấm lòng."
        ),

        // 3. Doctor #2
        User(
            id = 3,
            fullName = "Dr. Laura Phạm",
            roleId = 2,
            biography = "Bác sĩ tâm thần với kinh nghiệm điều trị rối loạn lo âu.",
            yearOfBirth = 1978,
            yearOfExperience = 18,
            avatarUrl = "https://s3-alpha-sig.figma.com/img/f953/bbe1/5370f69b9375f4ea05a5821cf8c9a75f?Expires=1745798400&Key-Pair-Id=APKAQ4GOSFWCW27IBOMQ&Signature=d7b5Poc46pQqjfSxjLzsEip4njMDwKfr-14Y4Yt~TwNcKaGivFcG~-HokJZhm93dfQuqknzjkaOpx8jEptyZAsYCwJwQjpABW0ynF3yKrVESWfeLXqVJsPLM8nMqiW8K3nHYmgbykIEuJkKEglmYsyXBsc-Lubq9paBzdnDo2Lhj7xWQ8TsIyy4UZh9dbtnomTeW5b5Z0uoHsr3wL-3CnfkkORvZDvOBqgD4b9uCOkH3OldPCGuT23qQskE6Iakd58bMw8C21LtoUzmUkj3qHTc0ETsDrohBDiLFF7AWrzx03JlZI6KdendUU5g7lMl2oFDKfzIoyFqYYAb4UifpAw__",
            backgroundUrl = "https://cdn.example.com/bgs/therapy_room.jpg",
            email = "laura.pham@healthapp.com",
            phone = "+84 936‑444‑555",
            password = "hashed_pw_laura",
            headline = "Mục tiêu của tôi là mang lại sự bình an cho bệnh nhân."
        ),

        // 2. Doctor #1
        User(
            id = 20,
            fullName = "Dr. Bình Trần",
            roleId = 2,                              // DOCTOR
            biography = "Chuyên gia tâm lý trị liệu hành vi.",
            yearOfBirth = 1981,
            yearOfExperience = 12,
            avatarUrl = "https://s3-alpha-sig.figma.com/img/820f/9108/f0790fb6290b22b04ac7f2fd03e7c1b5?Expires=1745798400&Key-Pair-Id=APKAQ4GOSFWCW27IBOMQ&Signature=ml9aruUXODk0bc53Kfab6W~2LtaAWoqDGQH6WdFA9PJ~oXySTo8lqBBJ-ckuoA8ilnAiwNNNHF5JRAGDoIAhz7ESENeQb6Qh3qmsweshlE4LrqApGeoKSzVvZJqoFklkcEesaRuK-YMOdP7bklNhZ9t92JgxQzKjRm8DBK-o9fyij4dOmLmo19Zz-MaJfOgrOPrZNVdPN8GCi395z~cu3y7mvrEDs~HAKUZkFzKqIBcUt-P~X3kenuzfEn-eUGR4Lq-QV7IZNijCBQGPPjrYvG7a1r7dymsvXjyn0Va-yqJcKAmO8wx2JbWKdus091XByFrNXsiOEtwRbMzvxIS6xw__",
            backgroundUrl = "https://cdn.example.com/bgs/clinic.jpg",
            email = "binh.tran@healthapp.com",
            phone = "+84 905‑222‑111",
            password = "hashed_pw_binh",
            headline = "Tôi tin rằng mỗi người đều có khả năng tự chữa lành."
        ),

        // 3. Doctor #2
        User(
            id = 30,
            fullName = "Dr. Laura Phạm",
            roleId = 2,
            biography = "Bác sĩ tâm thần với kinh nghiệm điều trị rối loạn lo âu.",
            yearOfBirth = 1978,
            yearOfExperience = 18,
            avatarUrl = "https://s3-alpha-sig.figma.com/img/f953/bbe1/5370f69b9375f4ea05a5821cf8c9a75f?Expires=1745798400&Key-Pair-Id=APKAQ4GOSFWCW27IBOMQ&Signature=d7b5Poc46pQqjfSxjLzsEip4njMDwKfr-14Y4Yt~TwNcKaGivFcG~-HokJZhm93dfQuqknzjkaOpx8jEptyZAsYCwJwQjpABW0ynF3yKrVESWfeLXqVJsPLM8nMqiW8K3nHYmgbykIEuJkKEglmYsyXBsc-Lubq9paBzdnDo2Lhj7xWQ8TsIyy4UZh9dbtnomTeW5b5Z0uoHsr3wL-3CnfkkORvZDvOBqgD4b9uCOkH3OldPCGuT23qQskE6Iakd58bMw8C21LtoUzmUkj3qHTc0ETsDrohBDiLFF7AWrzx03JlZI6KdendUU5g7lMl2oFDKfzIoyFqYYAb4UifpAw__",
            backgroundUrl = "https://cdn.example.com/bgs/therapy_room.jpg",
            email = "laura.pham@healthapp.com",
            phone = "+84 936‑444‑555",
            password = "hashed_pw_laura",
            headline = "Mục tiêu của tôi là mang lại sự bình an cho bệnh nhân."
        ),

        // 4. Patient #1
        User(
            id = 4,
            fullName = "Minh Hoàng",
            roleId = 3,                              // PATIENT
            biography = "Sinh viên năm 3 ngành CNTT.",
            yearOfBirth = 2002,
            avatarUrl = "https://cdn.example.com/avatars/minh.jpg",
            backgroundUrl = "https://cdn.example.com/bgs/city.jpg",
            email = "minh.hoang@example.com",
            phone = "+84 988‑777‑666",
            password = "hashed_pw_minh"
        ),

        // 5. Patient #2
        User(
            id = 5,
            fullName = "Hà My",
            roleId = 3,
            biography = "Nhân viên marketing yêu thích yoga và thiền.",
            yearOfBirth = 1996,
            avatarUrl = "https://cdn.example.com/avatars/hamy.jpg",
            backgroundUrl = "https://cdn.example.com/bgs/beach.jpg",
            email = "ha.my@example.com",
            phone = "+84 979‑888‑999",
            password = "hashed_pw_hamy"
        )
    )

    /* ---------- APPOINTMENTS ---------- */
//    val appointments = listOf(
//        Appointment(
//            id = 1,
//            patientId = 2,
//            doctorId = 1,
//            appointmentDate = LocalDate.now().plusDays(3),
//            appointmentTime = LocalTime.of(10, 0)
//        )
//    )

    /* ---------- COLLECTIONS ---------- */
    val collections = listOf(
        Collection(
            id = 1,
            content = "Lo‑fi Chill Mix",
            resourceId = "https://cdn.example.com/audio/lofi.mp3",
            topicId = 1,
            type = CollectionType.MUSIC
        ),
        Collection(
            id = 2,
            content = "Mindfulness Podcast Ep.1",
            resourceId = "https://cdn.example.com/podcast/ep1.mp3",
            topicId = 1,
            type = CollectionType.PODCAST
        )
    )

    /* ---------- TOPICS ---------- */
    val topics = listOf(
        Topic(
            id = 1,
            content = "Thư giãn & Ngủ ngon",
            avatarUrl = "https://cdn.example.com/img/sleep.png"
        )
    )

    val comments = listOf(
        Comment(
            id = 1,
            content = "Comment 1",
            imageUrl = "",
            userId = 1,
            postId = 1,
            reactCount = 20,
        ),
        Comment(
            id = 2,
            content = "Comment 2",
            imageUrl = "",
            userId = 2,
            postId = 1,
            reactCount = 2,
        ),
        Comment(
            id = 3,
            content = "Comment 3",
            imageUrl = "",
            userId = 3,
            postId = 1,
            reactCount = 11,
        )
    )

    val posts = listOf(
        Post(
            id = 1,
            content = "Short post abcxyz",
            posterId = 2,
            communityId = 1,
            visibility = PostVisibility.PUBLIC,
            imageUrl = "",
            reactCount = 123,
            createdAt = LocalDateTime.now()
        ),
        Post(
            id = 2,
            content = "Long post abcxyz \nSed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?",
            posterId = 2,
            communityId = 2,
            visibility = PostVisibility.PUBLIC,
            imageUrl = "",
            reactCount = 12,
            createdAt = LocalDateTime.now()
        )
    )

    val diaries = listOf(
        Diary(
            id = 1,
            emotion = Emotions.CONFUSE,
            title = "Nhật ký 1",
            content = "Long post abcxyz \nSed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?",
            imageUrl = null,
            voiceUrl = null,
            posterId = 2,
            createdAt = LocalDateTime.now()
        ),
        Diary(
            id = 2,
            emotion = Emotions.SAD,
            content = "abcxyz \nSed ut perspiciatis unde omnis iste architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?",
            imageUrl = null,
            voiceUrl = null,
            posterId = 2,
            createdAt = LocalDateTime.now()
        ),
        Diary(
            id = 3,
            emotion = Emotions.INLOVE,
            title = "Nhật ký 3",
            content = "Long post abcxyz \nSed ut perspiciatis unde omnis iste natus error ",
            imageUrl = null,
            voiceUrl = null,
            posterId = 2,
            createdAt = LocalDateTime.now()
        ),
        Diary(
            id = 4,
            title = "Nhật ký 4",
            emotion = Emotions.ANGRY,
            content = "Long post abcxyz \nSed ut perspiciatis unde omnis iste natus error sit",
            imageUrl = null,
            voiceUrl = null,
            posterId = 2,
            createdAt = LocalDateTime.now()
        ),
        Diary(
            id = 5,
            emotion = Emotions.CONFUSE,
            title = "Nhật ký 5",
            content = "Long post abcxyz \nSed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto ",
            imageUrl = null,
            voiceUrl = null,
            posterId = 2,
            createdAt = LocalDateTime.now()
        ),
        Diary(
            id = 6,
            emotion = Emotions.HAPPY,
            title = "Nhật ký 6",
            content = "Long post abcxyz \nSeodit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?",
            imageUrl = null,
            voiceUrl = null,
            posterId = 2,
            createdAt = LocalDateTime.now()
        ),
    )

}
