package com.dogrescue.config;

import com.dogrescue.entity.*;
import com.dogrescue.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DogService dogService;
    private final StoryService storyService;
    private final AdoptionService adoptionService;
    private final VolunteerService volunteerService;
    private final LostFoundService lostFoundService;
    private final DonationService donationService;

    private final Random random = new Random();

    // 本地品种图片映射（图片存放在 static/images/dogs/ 目录下）
    private static final String IMG_BASE = "/images/dogs/";
    private final Map<String, String[]> breedImageMap = new HashMap<>();

    public DataInitializer(DogService dogService, StoryService storyService, 
                          AdoptionService adoptionService, VolunteerService volunteerService,
                          LostFoundService lostFoundService, DonationService donationService) {
        this.dogService = dogService;
        this.storyService = storyService;
        this.adoptionService = adoptionService;
        this.volunteerService = volunteerService;
        this.lostFoundService = lostFoundService;
        this.donationService = donationService;
        initBreedImageMap();
    }

    /**
     * 初始化品种-本地图片精确映射
     * 图片文件由豆包AI生成，存放在 src/main/resources/static/images/dogs/ 下
     */
    private void initBreedImageMap() {
        // 20个品种，每个品种2张图片，按顺序一一对应 dog0-dog39
        // 图片由豆包AI生成，按品种顺序排列：每2张为一组，对应breeds数组中的品种
        breedImageMap.put("中华田园犬", arr("dog0.png", "dog1.png"));
        breedImageMap.put("金毛",       arr("dog2.png", "dog3.png"));
        breedImageMap.put("拉布拉多",   arr("dog4.png", "dog5.png"));
        breedImageMap.put("哈士奇",     arr("dog6.png", "dog7.png"));
        breedImageMap.put("萨摩耶",     arr("dog8.png", "dog9.png"));
        breedImageMap.put("泰迪",       arr("dog10.png", "dog11.png"));
        breedImageMap.put("比熊",       arr("dog12.png", "dog13.png"));
        breedImageMap.put("柯基",       arr("dog14.png", "dog15.png"));
        breedImageMap.put("边牧",       arr("dog16.png", "dog17.png"));
        breedImageMap.put("德牧",       arr("dog18.png", "dog19.png"));
        breedImageMap.put("阿拉斯加",   arr("dog20.png", "dog21.png"));
        breedImageMap.put("柴犬",       arr("dog22.png", "dog23.png"));
        breedImageMap.put("秋田犬",     arr("dog24.png", "dog25.png"));
        breedImageMap.put("斗牛犬",     arr("dog26.png", "dog27.png"));
        breedImageMap.put("法斗",       arr("dog28.png", "dog29.png"));
        breedImageMap.put("英斗",       arr("dog30.png", "dog31.png"));
        breedImageMap.put("雪纳瑞",     arr("dog32.png", "dog33.png"));
        breedImageMap.put("约克夏",     arr("dog34.png", "dog35.png"));
        breedImageMap.put("吉娃娃",     arr("dog36.png", "dog37.png"));
        breedImageMap.put("博美",       arr("dog38.png", "dog39.png"));
    }

    private String[] arr(String... names) {
        String[] urls = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            urls[i] = IMG_BASE + names[i];
        }
        return urls;
    }

    @Override
    public void run(String... args) throws Exception {
        // 强制重新生成数据（每次启动都会清空并重新生成）
        // 如果需要保留数据，请将下方代码注释掉
        System.out.println("开始清空旧数据并重新初始化...");
        try {
            // 先删除所有数据（按依赖关系倒序）
            donationService.remove(null);
            adoptionService.remove(null);
            volunteerService.remove(null);
            lostFoundService.remove(null);
            storyService.remove(null);
            dogService.remove(null);
            System.out.println("旧数据已清空");
        } catch (Exception e) {
            System.out.println("清空数据时出错（可能是表不存在）: " + e.getMessage());
        }

        System.out.println("开始初始化模拟数据...");
        initDogs();
        initStories();
        initAdoptions();
        initVolunteers();
        initLostFound();
        initDonations();
        System.out.println("模拟数据初始化完成！");
        System.out.println("总计生成：150只狗狗 + 80个故事 + 100条领养申请 + 60名志愿者 + 50条失物招领 + 120笔捐赠 = 560条数据");
    }

    private void initDogs() {
        List<Dog> dogs = new ArrayList<>();
        String[] names = {"小白", "大黄", "小黑", "花花", "豆豆", "球球", "旺财", "来福", "阿黄", "点点", 
                         "毛毛", "多多", "乐乐", "欢欢", "喜喜", "福福", "禄禄", "寿寿", "财财", "宝宝",
                         "贝贝", "晶晶", "莹莹", "琳琳", "娜娜", "莉莉", "莎莎", "美美", "丽丽", "婷婷",
                         "强强", "伟伟", "杰杰", "明明", "亮亮", "浩浩", "宇宇", "轩轩", "博博", "涛涛"};
        String[] breeds = {"中华田园犬", "金毛", "拉布拉多", "哈士奇", "萨摩耶", "泰迪", "比熊", "柯基", "边牧", "德牧",
                          "阿拉斯加", "柴犬", "秋田犬", "斗牛犬", "法斗", "英斗", "雪纳瑞", "约克夏", "吉娃娃", "博美"};
        String[] genders = {"公", "母"};
        String[] healthStatuses = {"健康", "轻微受伤", "需要治疗", "已康复", "疫苗接种中"};
        String[] statuses = {"待领养", "已领养", "治疗中", "隔离观察"};

        // 用于跟踪每个品种已分配的数量，确保同品种的不同狗狗使用不同图片
        Map<String, Integer> breedCounter = new HashMap<>();

        for (int i = 0; i < 150; i++) {
            Dog dog = new Dog();
            String breed = breeds[i % breeds.length]; // 确定性分配品种，确保每种品种都有狗狗
            dog.setName(names[random.nextInt(names.length)] + (i + 1));
            dog.setBreed(breed);
            dog.setGender(genders[random.nextInt(genders.length)]);
            dog.setAge(random.nextInt(15) + 1);
            dog.setHealthStatus(healthStatuses[random.nextInt(healthStatuses.length)]);
            dog.setDescription("这是一只可爱的" + breed + "，性格温顺，喜欢与人亲近。" +
                             "它曾经流浪在街头，现在得到了很好的照顾。" +
                             "希望能为它找到一个温暖的家。");
            // 根据品种从本地图片映射中分配对应的精确图片
            String[] breedImages = breedImageMap.get(breed);
            int count = breedCounter.getOrDefault(breed, 0);
            String primaryImage = breedImages[count % breedImages.length];
            String secondaryImage = breedImages[(count + 1) % breedImages.length];
            dog.setImageUrls(primaryImage + "," + secondaryImage);
            breedCounter.put(breed, count + 1);
            dog.setStatus(statuses[random.nextInt(statuses.length)]);
            dog.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(365)));
            dog.setUpdateTime(dog.getCreateTime());
            dogs.add(dog);
        }

        dogService.saveBatch(dogs);
        System.out.println("已添加 " + dogs.size() + " 只狗狗数据（品种与本地图片精确匹配）");
    }

    private void initStories() {
        List<Story> stories = new ArrayList<>();
        String[] titles = {"从流浪到重生：小白的故事", "爱心接力：救助受伤大黄", 
                          "温暖的家：豆豆领养记", "奇迹康复：花花的抗病之路",
                          "志愿者的日常：用爱守护每一个生命", "寻找主人：失散多年的团聚",
                          "狗狗的心理创伤修复", "如何照顾刚救助的流浪狗",
                          "流浪狗救助站的日与夜", "领养代替购买的意义",
                          "一只流浪狗的逆袭人生", "救助站里的感人故事",
                          "用镜头记录流浪狗的日常", "爱心人士的无私奉献",
                          "狗狗康复训练的重要性", "冬季救助行动纪实"};
        String[] authors = {"爱心志愿者", "救助站工作人员", "领养家庭", "动物保护协会", 
                           "热心市民", "兽医专家", "公益组织", "爱心人士"};
        
        // 故事封面图片 - 使用本地狗狗图片
        String[] storyImages = {
            IMG_BASE + "dog2.png", IMG_BASE + "dog6.png", IMG_BASE + "dog8.png",
            IMG_BASE + "dog10.png", IMG_BASE + "dog14.png", IMG_BASE + "dog16.png",
            IMG_BASE + "dog22.png", IMG_BASE + "dog24.png", IMG_BASE + "dog32.png",
            IMG_BASE + "dog38.png"
        };

        for (int i = 0; i < 80; i++) {
            Story story = new Story();
            story.setTitle(titles[i % titles.length] + (i > 15 ? "（" + (i/15 + 1) + "）" : ""));
            story.setContent("这是一个关于流浪狗救助的真实故事。\n\n" +
                           "在我们的救助站里，每天都有新的生命到来。它们曾经流浪在街头，" +
                           "饱受饥饿和寒冷的折磨。但是，在志愿者和爱心人士的帮助下，" +
                           "它们重新获得了温暖和关爱。\n\n" +
                           "每一只狗狗都有自己的故事，有的是被主人遗弃，有的是走失，" +
                           "有的是从不良繁殖场解救出来。无论它们经历了什么，" +
                           "我们都相信，爱能治愈一切创伤。\n\n" +
                           "通过这个故事，我们希望能唤起更多人对流浪动物的关注，" +
                           "用领养代替购买，给这些可怜的生命一个重新开始的机会。");
            story.setCoverImage(storyImages[i % storyImages.length]);
            story.setAuthor(authors[random.nextInt(authors.length)]);
            story.setPublishTime(LocalDateTime.now().minusDays(random.nextInt(365)));
            stories.add(story);
        }

        storyService.saveBatch(stories);
        System.out.println("已添加 " + stories.size() + " 个故事数据");
    }

    private void initAdoptions() {
        List<Adoption> adoptions = new ArrayList<>();
        String[] names = {"张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十",
                         "郑一", "冯二", "陈三", "褚四", "卫五", "蒋六", "沈七", "韩八",
                         "杨九", "朱十", "秦一", "许二"};
        String[] housingTypes = {"公寓", "别墅", "平房", "小区", "农村院落"};
        String[] statuses = {"待审核", "已通过", "已拒绝", "已完成"};

        for (int i = 0; i < 100; i++) {
            Adoption adoption = new Adoption();
            adoption.setDogId((long)(random.nextInt(150) + 1));
            adoption.setApplicantName(names[random.nextInt(names.length)] + (i + 1));
            adoption.setPhone("138" + String.format("%08d", random.nextInt(100000000)));
            adoption.setAddress("北京市朝阳区" + random.nextInt(100) + "号");
            adoption.setHousingType(housingTypes[random.nextInt(housingTypes.length)]);
            adoption.setHasExperience(random.nextBoolean());
            adoption.setReason("我一直很喜欢狗狗，家里有足够的时间和空间照顾它。" +
                             "我会给它提供一个温暖的家，定期带它体检，保证它的健康。");
            adoption.setStatus(statuses[random.nextInt(statuses.length)]);
            adoption.setRemark("申请理由充分，家庭环境适合养狗");
            adoption.setApplyTime(LocalDateTime.now().minusDays(random.nextInt(365)));
            adoption.setAuditTime(adoption.getApplyTime().plusDays(random.nextInt(7)));
            adoptions.add(adoption);
        }

        adoptionService.saveBatch(adoptions);
        System.out.println("已添加 " + adoptions.size() + " 条领养申请数据");
    }

    private void initVolunteers() {
        List<Volunteer> volunteers = new ArrayList<>();
        String[] names = {"志愿者小明", "志愿者小红", "志愿者小刚", "志愿者小丽", "志愿者小强",
                         "志愿者小芳", "志愿者小杰", "志愿者小敏", "志愿者小伟", "志愿者小静"};
        String[] skills = {"狗狗训练", "医疗护理", "清洁打扫", "摄影记录", "宣传推广",
                          "活动组织", "心理疏导", "营养配餐"};
        String[] availableTimes = {"周末全天", "工作日晚上", "节假日", "灵活时间", "上午时段", "下午时段"};
        String[] statuses = {"待审核", "已通过", "已拒绝", "活跃中"};

        for (int i = 0; i < 60; i++) {
            Volunteer volunteer = new Volunteer();
            volunteer.setName(names[random.nextInt(names.length)] + (i + 1));
            volunteer.setPhone("139" + String.format("%08d", random.nextInt(100000000)));
            volunteer.setEmail("volunteer" + i + "@example.com");
            volunteer.setSkills(skills[random.nextInt(skills.length)]);
            volunteer.setAvailableTime(availableTimes[random.nextInt(availableTimes.length)]);
            volunteer.setStatus(statuses[random.nextInt(statuses.length)]);
            volunteer.setApplyTime(LocalDateTime.now().minusDays(random.nextInt(365)));
            volunteers.add(volunteer);
        }

        volunteerService.saveBatch(volunteers);
        System.out.println("已添加 " + volunteers.size() + " 条志愿者数据");
    }

    private void initLostFound() {
        List<LostFound> lostFounds = new ArrayList<>();
        String[] types = {"寻狗启事", "发现流浪狗"};
        String[] titles = {"寻找走失的金毛犬", "发现受伤的小狗", "寻找黑色泰迪", 
                          "发现流浪母狗带小狗", "寻找走失的哈士奇", "发现生病的流浪狗"};
        String[] locations = {"朝阳区公园", "海淀区街道", "西城区小区", "东城区商场附近", 
                             "丰台区学校周边", "通州区河边"};
        String[] contactNames = {"张先生", "李女士", "王先生", "赵女士", "刘先生"};
        String[] statuses = {"进行中", "已找到", "已结束"};
        
        // 失物招领图片 - 使用本地狗狗图片
        String[] lostFoundImages = {
            IMG_BASE + "dog6.png", IMG_BASE + "dog3.png", IMG_BASE + "dog11.png",
            IMG_BASE + "dog21.png", IMG_BASE + "dog25.png", IMG_BASE + "dog37.png"
        };

        for (int i = 0; i < 50; i++) {
            LostFound lostFound = new LostFound();
            lostFound.setType(types[random.nextInt(types.length)]);
            lostFound.setTitle(titles[i % titles.length] + (i > 5 ? "（" + (i/5 + 1) + "）" : ""));
            lostFound.setDescription("在这里发现了/丢失了一只狗狗，特征是毛色为棕色，体型中等，" +
                                   "看起来很温顺。希望有缘人能够联系，让它早日回家/得到救助。");
            lostFound.setLocation(locations[random.nextInt(locations.length)]);
            lostFound.setContactName(contactNames[random.nextInt(contactNames.length)]);
            lostFound.setContactPhone("137" + String.format("%08d", random.nextInt(100000000)));
            lostFound.setImageUrls(lostFoundImages[i % lostFoundImages.length]);
            lostFound.setStatus(statuses[random.nextInt(statuses.length)]);
            lostFound.setEventTime(LocalDateTime.now().minusDays(random.nextInt(365)));
            lostFound.setCreateTime(lostFound.getEventTime());
            lostFounds.add(lostFound);
        }

        lostFoundService.saveBatch(lostFounds);
        System.out.println("已添加 " + lostFounds.size() + " 条失物招领数据");
    }

    private void initDonations() {
        List<Donation> donations = new ArrayList<>();
        String[] donorNames = {"爱心人士", "匿名捐赠者", "爱心企业", "公益组织", "热心市民", "爱心人士"};
        String[] paymentMethods = {"微信支付", "支付宝", "银行转账", "现金"};
        String[] statuses = {"待支付", "已支付", "已完成", "已取消"};

        for (int i = 0; i < 120; i++) {
            Donation donation = new Donation();
            donation.setDonorName(donorNames[random.nextInt(donorNames.length)] + (i + 1));
            donation.setAmount(new BigDecimal(random.nextInt(999) + 1).setScale(2, BigDecimal.ROUND_HALF_UP));
            donation.setPaymentMethod(paymentMethods[random.nextInt(paymentMethods.length)]);
            donation.setTransactionId("TXN" + System.currentTimeMillis() + random.nextInt(10000));
            donation.setStatus(statuses[random.nextInt(statuses.length)]);
            donation.setDonateTime(LocalDateTime.now().minusDays(random.nextInt(365)));
            donations.add(donation);
        }

        donationService.saveBatch(donations);
        System.out.println("已添加 " + donations.size() + " 条捐赠数据");
    }
}
