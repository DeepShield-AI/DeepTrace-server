@WebMvcTest(EsEdgesController.class)
@Import(TestConfig.class)
class EsEdgesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EsEdgeService esEdgeService;

    // 测试方法...
}
