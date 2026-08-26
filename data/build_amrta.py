# -*- coding: utf-8 -*-
import json, re

def clean(s):
    s = s.replace('\u200b','')
    # collapse doubled matras / vowel signs from PDF text-layer artifacts
    for a,b in [('ाा','ा'),('ेे','े'),('ोो','ो'),('ूू','ू'),('ृृ','ृ'),('ैै','ै'),('ंं','ं'),('ीी','ी'),('ुु','ु')]:
        s = s.replace(a,b)
    return s.strip()

E = []  # entries
def add(n, phon, phonr, name, namer, dev, tr, en, sec):
    E.append({"n":n,"section":sec,"phoneme":phon,"phoneme_roman":phonr,
              "name":clean(name),"name_roman":namer,
              "devanagari":clean(dev),"transliteration":clean(tr),"translation":clean(en)})

# ---------------- 16 AMRTA BHAIRAVAS (vowels) ----------------
add(1,'अ','a','अमृतभैरव','Amṛtabhairava',
 "अमृतमनन्तमनुत्तरमघोरषोडशकशक्तिचक्रगतम् । औन्मनसपदनिरूढिप्रथमोपोद्घातकं वन्दे ॥",
 "amṛtam anantam anuttaram aghoraṣoḍaśakaśakticakragatam । aunmanasapadanirūḍhiprathamopodghātakaṃ vande ॥",
 "I surrender to Amṛta [Bhairava], who is endless and unsurpassable [or the phoneme 'A'], and who is at the center of the sixteen powers in the wheel of Aghora and who is the first throb in being established in the state transcending the mind [or being in the state of unmanā].",
 "vowels")
add(2,'आ','ā','अमृतपूर्ण','Amṛtapūrṇa',
 "आनन्दममृतपूर्णं सामनसे परपदे परं सत्यम् । घटितानुत्तरदृढतमनिरूढिभाजं शिवं वन्दे ॥",
 "ānandam amṛtapūrṇaṃ sāmanase parapade paraṃ satyam । ghaṭitānuttaradṛḍhatamanirūḍhibhājaṃ śivaṃ vande ॥",
 "I surrender to Amṛtapūrṇa Śiva, who is identical to [nija-]-ānanda [the surge of bliss or the expression of fullness] [or the phoneme 'ā'], the alternative truth [of relativity or being in the world] in [His] other state of [being in] Samanā [the last station for mentation], who is firmly recognized as the constructed state of the absolute [or anuttara].",
 "vowels")
add(3,'इ','i','अमृताभ','Amṛtābha',
 "इच्छाशक्तिसुनिर्भरममृताभमनन्तभुवनजननपटुम् । वन्दे स्वशक्तिलहरीबहलितभैरवपरानन्दम् ॥",
 "icchāśaktisunirbharam amṛtābham anantabhuvanajananapaṭum । vande svaśaktilaharībahalitabhairavaparānandam ॥",
 "I surrender to Amṛtābha, who is suffused with the power of volition [or an expression of the phoneme 'Ī'], who is skilled in creating an endless number of worlds, and is the next bliss or the fullness [in the form of Nirānanda] that is fortified with the waves of his own potencies.",
 "vowels")
add(4,'ई','ī','अमृतद्रव','Amṛtadrava',
 "ईश्वरमशेषतापप्रशमनममृतद्रवं सदा वन्दे । अप्रतिघातिस्वेच्छाविकासविश्रान्तममृतकरमौलिम् ॥",
 "īśvaram aśeṣatāpapraśamanam amṛtadravaṃ sadā vande । apratighātisvecchāvikāsaviśrāntam amṛtakaramaulim ॥",
 "I always surrender to Amṛtadrava, [identical to the principle] Īśvara, who pacifies all forms of heat [or all suffering], who is the counter-image of the absolute or the phoneme 'a' and who is resting on [the state of] the blossoming of boundless volition [or the phoneme 'Ī'], [and] who wears [the moon or] the rays of nectar as his crest jewel.",
 "vowels")
add(5,'उ','u','अमृतौघ','Amṛtaugḥa',
 "यदनुत्तरसम्बोधादानन्दविकस्वरेच्छया पूर्णम् । ईश्वरमुन्मिषदमृतौघसुन्दरं तत्स्तुवे धाम ॥",
 "yad anuttarasambodhād ānandavikasvarecchayā pūrṇam । īśvaram unmiṣadamṛtaughasundaraṃ tatstuve dhāma ॥",
 "I praise the state of beautiful Amṛtaugha, the expressive mode [identified with the phoneme 'U'] of Īśvara, who is replete with volition [identified with the phoneme 'Ia'] or the expressed state of fullness [or bliss] due to the recognition of the absolute [identified with the phoneme 'A'].",
 "vowels")
add(6,'ऊ','ū','अमृतोर्मि','Amṛtormi',
 "अहमानन्दघनेच्छाघटितेश्वरतोन्मिषत्समस्तोर्मिः । इत्युल्लासतरङ्गितममृतोर्मिमहं चिदर्णवं वन्दे ॥",
 "aham ānandaghana icchāghaṭiteśvaratonmiṣatsamastormiḥ । ity ullāsataraṅgitam amṛtormim ahaṃ cidarṇavaṃ vande ॥",
 "I surrender to Amṛtormi, the ocean of consciousness who is splashing with the tides of expression [or the state of bliss in the form of mahānanda], where the entirety of the tides [or the phoneme 'ū' that manifests by being the phoneme 'u' combined] that is expressing [or manifesting the phoneme 'U'] the principle [of] Īśvara, an embodiment of volition [or composed of the phoneme 'i'], which in turn is the mass of bliss [or the expression of the phoneme 'ā'] corresponding to [the absolute] 'I-am' [or pūrṇāhantā].",
 "vowels")
add(7,'ऋ','ṛ','अमृतस्यन्दन','Amṛtasyandana',
 "स्वप्रसरप्रेङ्खितविलसदूर्मिसंक्षुभितचिद्रसापूरम् । अमृतस्यन्दनसारं भैरवसंविन्महार्णवं वन्दे ॥",
 "svaprasarapreṅkhitavilasadūrmisaṅkṣubhitacidrasāpūram । amṛtasyandanasāraṃ bhairavasaṃvinmahārṇavaṃ vande ॥",
 "I surrender to the great ocean of Bhairava consciousness [or the consciousness of the totality as I-am] that is the essence of the flow of the nectar, [Lord] Amṛtasyandana, who is replete with the rasa of consciousness [representing the first of the nectar-representing phonemes of 'ṛ'], [or replete with Cidānanda, the sixth state of bliss] caused by the perturbed tides [or the expression of the phoneme 'ū'] that are propelled by the expansion of the self [represented by the phoneme 'a'].",
 "vowels")
add(8,'ॠ','ṝ','अमृताङ्गद','Amṛtāṅgada',
 "पूर्वं यदनुत्तरममृतभूमिमासाद्य सप्तमीं कलनाम् । विश्राम्यति तत्प्रणमाम्यमृताङ्गदं परानन्दि ॥",
 "pūrvaṃ yad anuttaram amṛtabhūmim āsādya saptamīṃ kalanām । viśrāmyati tat praṇamāmy amṛtāṅgadaṃ parānandi ॥",
 "I surrender to Amṛtāṅgada, [who bestows the nectar body representing the phoneme long vowel 'Ṛ'] in absolute fullness [representing jagadānanda or the blossoming or the bliss of the totality] that comes to repose after having reached the seventh state of the nectar [or the phoneme 'ṛ'] which, in its initial state, was the very absolute state [or the phoneme 'a'].",
 "vowels")
add(9,'ऌ','ḷ','अमृतवपुष्','Amṛtavapuṣ',
 "शिवममृतवपुषममृतकलाचतुष्टयतृतीयभागजुषम् । प्रणमामि भासयन्तं क्रमरहितेऽपि क्रममनेकम् ॥",
 "śivam amṛtavapuṣam amṛtakalācatuṣṭayatṛtīyabhāgajuṣam । praṇamāmi bhāsayantaṃ kramarahite'pi kramamanekam ॥",
 "I surrender to the auspicious Amṛtavapuṣ who adorns the third part of the four aspects of nectar [or the third among four Amṛta vowels, namely 'ḷ'], who manifests manifold successions, even where there is no succession.",
 "vowels")
add(10,'ॡ','ḹ','अमृतोद्गार','Amṛtodgāra',
 "संजीवनतुर्यकलाकलितविबोधं समस्तभावानाम् । दूषणविषशीर्णानाममृतोद्गारं शिवं वन्दे ॥",
 "saṃjīvanaturyakalākalitavibodhaṃ samastabhāvānām । dūṣaṇaviṣaśīrṇānām amṛtodgāraṃ śivaṃ vande ॥",
 "I surrender to the auspicious Amṛtodgāra, who brings to awakening all the entities that are emaciated by the poison of defects [of the loss of self-identity as Śiva], being determined by the fourth aspect of the nectar [or the tenth phoneme 'Ḷ'].",
 "vowels")
add(11,'ए','e','अमृतास्य','Amṛtāsya',
 "एकमनुत्तररूपात्प्रभृतित्रिकशक्तिपूरितानन्दम् । अमृतास्यमस्य जगतः प्रमाणभूतं शिवं वन्दे ॥",
 "ekam anuttararūpāt prabhṛti trikaśaktipūritānandam । amṛtāsyam asya jagataḥ pramāṇabhūtaṃ śivaṃ vande ॥",
 "I surrender to the auspicious Amṛtāsya, who has assumed the form of the means of cognition [or who has become the pramāṇa] of the entirety of the world, who is of the character of bliss that is replete with the triadic potencies [expressed by the triadic deities Parā, Parāparā, and Aparā] [or who represents the triangular structure] that initiates with the absolute [or the phoneme 'a'] when it is merged with volition or the phoneme 'i'.",
 "vowels")
add(12,'ऐ','ai','अमृततनु','Amṛtatanu',
 "ऐक्यपरमार्थकलया त्रिशक्तियुगघटितवैश्वरूप्यमहम् । अमृततनुमतनुबोधप्रसरमहाकारणं स्मरामि हरम् ॥",
 "aikyaparamārthakalayā triśaktiyugaghaṭitavaiśvarūpyam aham । amṛtatanum atanubodhaprasaramahākāraṇaṃ smarāmi haram ॥",
 "I visualize Amṛtatanu, the remover [of all suffering], who is the absolute cause for the flow, the awakening that 'I am not determined within the epidermis' (atanubodha), the very 'I-am' that is expressed in every single form [or that encompasses everything] composed with the fusion of the triadic potencies [or the expressed form of the phoneme 'e' represented by the 'ai'], by means of the aspects whose essential nature is oneness [or the phoneme 'ai'].",
 "vowels")
add(13,'ओ','o','अमृतसेचन','Amṛtasecana',
 "ओतप्रोतं सकलं विद्ध्वा स्वरसेन शिवमयीकुरुते । योऽनुत्तरधाम्न्युदयन्स्वयममृतनिषेचनं तमस्मि नतः ॥",
 "otaprotam sakalaṃ viddhvā svarasena śivamayīkurute । yo'nuttaradhāmny udayan svayam amṛtaniṣecanaṃ tam asmi nataḥ ॥",
 "I surrender to Amṛtasecana, [who is saturated with] nectar, who himself emerges in the state of the absolute [or the initial phoneme of 'a'], who replenishes everything [by means of the vowel 'O'] with his own rasa and transforms all into Śiva nature.",
 "vowels")
add(14,'औ','au','अमृतमूर्ति','Amṛtamūrti',
 "औषधमाधिव्याधिषु पाशत्रयशातनं त्रिशूलकरम् । वन्देऽहममृतमूर्तिं पूर्णत्रिकशक्तिपरमार्थम् ॥",
 "auṣadham ādhivyādhiṣu pāśatrayaśātanaṃ triśūlakaram । vande'ham amṛtamūrtiṃ pūrṇatrikaśaktiparamārtham ॥",
 "I surrender to Amṛtamūrti, the embodiment of ambrosia, who is the essential nature of the saturated form of the triadic potencies [expressed in the phoneme of 'O'], who carries a trident that destroys threefold snares [of rāga, dveṣa, and moha], and who is the healing medicine [or the phoneme 'Au'] for mental and physical suffering.",
 "vowels")
add(15,'अं','aṃ','अमृतेश','Amṛteśa',
 "बैन्दवममृतरसमयं वेद्यं योऽनुत्तरे निजे धाम्नि । पूर्णीभावयतितमाममृतेशं तं नमस्यामि ॥",
 "baindavam amṛtarasamayaṃ vedyaṃ yo'nuttare nije dhāmni । pūrṇībhāvayatitamām amṛteśaṃ taṃ namasyāmi ॥",
 "I surrender to Amṛteśa, who brings [the entirety of] the entities to be cognized to absolute fullness within his own state of the absolute within a singular drop [signified by the vowel 'am' and marked with a single drop '.'], having transformed them into the fluid of nectar.",
 "vowels")
add(16,'अः','aḥ','अमृतधर','Amṛtadhara',
 "प्रसृतमनुत्तररूपादानन्दादिक्रमेण विश्वमदः । सर्वामृतधरमन्तर्बहिश्च विसृजन्तमभिवन्दे ॥",
 "prasṛtam anuttararūpād ānandādikrameṇa viśvam adaḥ । sarvāmṛtadharam antarbahiś ca visṛjantam abhivande ॥",
 "I surrender to Amṛtadhara [represented by the last vowel ':'], who emits the world in the initial stage internally and externally, [as expressed by the two drops] that flow from its initial form of the absolute [represented by the phoneme 'a'] in the sequence of [the expression of] bliss [that is marked by the phoneme 'ā'].",
 "vowels")

# ---------------- 34 RUDRA BHAIRAVAS (consonants) ----------------
R = [
(17,'क','ka','जय [रुद्र]','Jaya [Rudra]',
 "जयतां नतजनजयकृत् स जयो रुद्रो विनाभ्युपायं यः । पूरयति कं न कामं कामं कामेश्वरत्वेन ॥",
 "jayatāṃ natajanajayakṛt sa jayo rudro vinābhyupāyaṃ yaḥ । pūrayati kaṃ na kāmaṃ kāmaṃ kāmeśvaratvena ॥",
 "Victorious is Jaya Rudra, who grants the victory [of recognizing oneself as Śiva], without even relying on any means [referring to anupāya or the sudden flash of recognition] to those who surrender to him, who at will fulfills any [also referring to the phoneme 'ka'] desires, as he himself is the lord of desire or Kāmeśvara."),
(18,'ख','kha','विजय [रुद्र]','Vijaya [Rudra]',
 "खात्मत्वेऽपि विचित्रं निखिलमिदं वाच्यवाचकात्म जगत् । दर्पणनगरवदात्मनि विभासयन्विजयते विजयः ॥",
 "khātmatve'pi vicitraṃ nikhilam idaṃ vācyavācakātma jagat । darpaṇanagaravad ātmani vibhāsayan vijayate vijayaḥ ॥",
 "Victorious is Vijaya, who manifests [by means of Śāṃbhavaupāya] within himself the entirety of this manifold world of the nature of the expressive speech and the expressed objects, even though his essential nature is of the character of the empty space [of consciousness, also referring to the phoneme 'kha'], similar to the city reflected in a mirror."),
(19,'ग','ga','जयन्त [रुद्र]','Jayanta [Rudra]',
 "यो दुर्विकल्पविघ्नविध्वंसे सद्विकल्पगणपतिताम् । वहति जयताज्जयन्तः स परं परममन्त्रवीर्यात्मा ॥",
 "yo durvikalpavighnavidhvaṃse sadvikalpagaṇapatitām । vahati jayatāj jayantaḥ sa paraṃ paramamantravīryātmā ॥",
 "Victorious is Jayanta, who assumes the form of Gaṇeśa [also referring to the phoneme 'ga'] that stands for correct mental construction [or the Śākta means of the purification of vikalpas] for the sake of destroying the obstacles of disruptive mental constructions. He, while being of the essence of the vigor of the supreme mantras, is himself transcendent."),
(20,'घ','gha','अपराजित [रुद्र]','Aparājita [Rudra]',
 "यो नाम घोरनिनदोच्चारवशाद्भीषयत्यशेषजगत् । स्वस्थानध्यानरतः स जयत्यपराजितो रुद्रः ॥",
 "yo nāma ghoraninadoccāravaśād bhīṣayaty aśeṣajagat । svasthānadhyānarataḥ sa jayaty aparājito rudraḥ ॥",
 "Victorious is Aparājita Rudra, who is engaged in visualization in the sites [of prāṇa, body, and outside in the dwellings of Yoginīs], who terrifies the entirety of the world [that is extroverted, separated from the Self-nature] by means of the articulation of fearsome sounds [also referring to the phoneme 'gha,' or the expression of the eightfold potencies within the group of Parāparā, beginning with Aghorā, referring to Āṇava Upāya]."),
(21,'ङ','ṅa','सुजय [रुद्र]','Sujaya [Rudra]',
 "कवलयितुं किल कालं कलयति यो व्यायतास्यतां सततम् । जयति स सुजयः साक्षात्संसारपराकृतौ सजयः ॥",
 "kavalayituṃ kila kālaṃ kalayati yo vyāyatāsyatāṃ satatam । jayati sa sujayaḥ sākṣāt saṃsāraparākṛtau sajayaḥ ॥",
 "Victorious is Sujaya, who has directly attained victory in defeating the world [of suffering due to misconception], and who always assumes the form of a widely-opened mouth in order to swallow time [in the course of kāla-grāsa]."),
(22,'च','ca','जयरुद्र','Jayarudra',
 "तत्तन्मन्त्राभ्युदयप्रगुणीकृतचण्डभैरवावेशः । विद्रावितभवमुद्रो द्रढयतु भद्राणि जयरुद्रः ॥",
 "tattanmantrābhyudayapraguṇīkṛtacaṇḍabhairavāveśaḥ । vidrāvitabhavamudro draḍhayatu bhadrāṇi jayarudraḥ ॥",
 "May Jayarudra fortify my virtues, [He] who displays the gesture of melting the world [or the perception of duality], whose entry into ferocious Caṇḍa Bhairava [also referring to the phoneme 'ca'] has been deepened due to the rise of the corresponding mantras."),
(23,'छ','cha','जयकीर्ति','Jayakīrti',
 "जयकीर्तिरयं जयताज्जगदम्भोजं विभक्तभुवनदलम् । रविरिव विकासयति यश्चिदेकनालाश्रयत्वेन ॥",
 "jayakīrtir ayaṃ jayatāj jagadambhojaṃ vibhaktabhuvanadalam । ravir iva vikāsayati yaś cidekanālāśrayatvena ॥",
 "May Jayakīrti be victorious, who causes the blossoming of the lotus-like world with different realms as its petals, like the sun by means of resting on the singular stalk [or the hub] of consciousness."),
(24,'ज','ja','जयावह','Jayāvaha',
 "तत्त्वक्रमावभासनविभागविभवो भुजङ्गमाभरणः । भक्तजनजयावहतां वहति जयावहो जयति ॥",
 "tattvakramāvabhāsanavibhāgavibhavo bhujaṅgamābharaṇaḥ । bhaktajanajayāvahatāṃ vahati jayāvaho jayati ॥",
 "Victorious is Jayāvaha, who carries victory [also referring to the phoneme 'ja'] for those who praise him, whose glory lies in differentiating between the manifestation of the succession of the principles and he who is embellished with a snake."),
(25,'झ','jha','जयमूर्ति','Jayamūrti',
 "तत्तत्तत्त्वविभेदनसमुद्द्योतिनिशितशूलकरः । जयति परं जयमूर्तिः संसारपराजयस्फूर्तिः ॥",
 "tattattattvavibhedanasamuddyotiniśitaśūlakaraḥ । jayati paraṃ jayamūrtiḥ saṃsāraparājayasphūrtiḥ ॥",
 "Victorious is the supreme Jayamūrti, the expressed form of the defeat of the world [composed of duality], who carries on his hand a sharp spear that is shining forth in differentiating the principles accordingly."),
(26,'ञ','ña','जयोत्साह','Jayotsāha',
 "स्वात्ममहाभीमरवामर्शनवशशकलिताध्वसन्तानः । भवदुर्गभञ्जनजयोत्साहो जयताज्जयोत्साहः ॥",
 "svātmamahābhīmaravāmarśanavaśaśakalitādhvasantānaḥ । bhavadurgabhañjanajayotsāho jayatāj jayotsāhaḥ ॥",
 "May Jayotsāha be victorious, who is joyous due to victory in destroying the fortress of becoming [or being distinct from Śiva], who has differentiated the collection of paths [divided as time and space which are further divided] by means of reflexively cognizing a loud and fearsome sound within oneself."),
(27,'ट','ṭa','जयद','Jayada',
 "अमृतात्मकार्धचन्द्रप्रगुणाभरणोऽध्वमण्डलं निखिलम् । विश्रमयन्निजसंविदि जयदोऽस्तु सतां सदा जयदः ॥",
 "amṛtātmakārdhacandrapraguṇābharaṇo'dhvamaṇḍalaṃ nikhilam । viśramayan nijasaṃvidi jayado'stu satāṃ sadā jayadaḥ ॥",
 "May Jayada always be the bestower of victory to virtuous [people], who is adorned with marvellous crescent moon, [who is] of the character of nectar, who comes to rest within his own pure consciousness, [dissolving] the entirety of the circle of categories [or the paths or adhvan]."),
(28,'ठ','ṭha','जयवर्धन','Jayavardhana',
 "जयवर्धनः सुखर्द्धिं वर्धयतात्पूर्णचन्द्रविशदगतिः । आप्याययति जगद्यः स्वशक्तिपातामृतासारैः ॥",
 "jayavardhanaḥ sukharddhiṃ vardhayatāt pūrṇacandraviśadagatiḥ । āpyāyayati jagad yaḥ svaśaktipātāmṛtāsāraiḥ ॥",
 "May Jayavardhana, who is in the state [of fullness comparable to] the full moon, who saturates the world with the shower of the nectar of the emission of the potencies that are inherent with him: may he increase happiness and perfection."),
(29,'ड','ḍa','बल','Bala',
 "यो योगिनीप्रियतया तिरोहितिव्यपगतिक्रमं जगताम् । प्रबलीकरोति बलतो बलाय तस्मै बलिं यामः ॥",
 "yo yoginīpriyatayā tirohitivyapagatikramaṃ jagatām । prabalīkaroti balato balāya tasmai baliṃ yāmaḥ ॥",
 "Let us offer ourselves to Bala, who, by being loved by Yoginīs, forcefully empowers the success of the removal of the concealment of the world."),
(30,'ढ','ḍha','अतिबल','Atibala',
 "यः परमेशसपर्याक्रियोपदेशाङ्कुशेन भवकरिणम् । कृतवानतिबलमतिबलमस्मि नतः फणभृदाभरणम् ॥",
 "yaḥ parameśasaparyākriyopadeśāṅkuśena bhavakariṇam । kṛtavān atibalam atibalam asmi nataḥ phaṇabhṛdābharaṇam ॥",
 "I surrender to Atibala, who subdues the world of becoming. [He is] ornamented with snakes, and like a wild elephant [is taught] by use of the goad, instructs the kriyās of worshipping the supreme Lord."),
(31,'ण','ṇa','बलभद्र','Balabhadra',
 "प्रणमामि निखिलपाशप्रवाहसंभेदभेदबलभद्रम् । बलभद्रं प्राणाश्वप्रचारचातुर्यपूर्णबलम् ॥",
 "praṇamāmi nikhilapāśapravāhasaṃbhedabhedabalabhadram । balabhadraṃ prāṇāśvapracāracāturyapūrṇabalam ॥",
 "I surrender to Balabhadra, who is [like] a mighty bull in destroying the different courses of all fetters, and [who] is imbued with strength due to his skill in controlling the horses of prāṇa."),
(32,'त','ta','बलप्रद','Balaprada',
 "निजशक्तिजनितकर्मप्रपञ्चसञ्चारचातुरीविभवम् । भवतरणबलप्रदतां समावहन्तं बलप्रदं नौमि ॥",
 "nijaśaktijanitakarmaprapañcasañcāracāturīvibhavam । bhavataraṇabalapradatāṃ samāvahantaṃ balapradaṃ naumi ॥",
 "I surrender to Balaprada, who procures prosperity by channeling skills over prapañca born of the acts of one's potencies, and who bestows strength to cross over [the cycles of] becoming."),
(33,'थ','tha','बलावह','Balāvaha',
 "ऋतधामानमनन्तं बलावहं तं बलावहं वन्दे । जगदिदममन्दमखिलं स्वमहिम्ना योऽनुगृह्णाति ॥",
 "ṛtadhāmānam anantaṃ balāvahaṃ taṃ balāvahaṃ vande । jagad idam amandam akhilaṃ svamahimnā yo'nugṛhṇāti ॥",
 "I surrender to Balāvaha, the bestower of strength, who is [like] Ananta in upholding truth [like Ananta when carrying Lord Viṣṇu], and who upholds this entire vibrant world with his own glory."),
(34,'द','da','बलवान्','Balavān',
 "भवभेदविभवसंभवसंभेदविभेदबलवन्तम् । बलवन्तं नौमि विभुं दारुणरूपग्रहाग्रहतः ॥",
 "bhavabhedavibhavasaṃbhavasaṃbhedavibhedabalavantam । balavantaṃ naumi vibhuṃ dāruṇarūpagrahāgrahataḥ ॥",
 "I surrender to the all-encompassing Balavān, who is powerful in associating and dissociating, and creating and destroying difference in the world by means of assuming and rejecting horrific manifestations."),
(35,'ध','dha','बलदाता','Baladātā',
 "जयति विभुर्बलदाता मूढजनाश्वासदायि येन वपुः । बहिराद्यन्तवदपि मध्यशून्यमुल्लासितं सततम् ॥",
 "jayati vibhur baladātā mūḍhajanāśvāsadāyi yena vapuḥ । bahir ādyantavad api madhyaśūnyam ullāsitaṃ satatam ॥",
 "Victorious is the empowering Baladātṛ who gives hope [even] to ignorant beings. He has manifest his form even in the empty state in the middle [of the path of prāṇa], just like inside [the mind] or outside."),
(36,'न','na','बलेश्वर','Baleśvara',
 "भेदप्रथाविलापनबलेश्वरं तं बलेश्वरं वन्दे । यः सकलाकलयोरपि मितात्मताया निषेधमादध्यात् ॥",
 "bhedaprathāvilāpanabaleśvaraṃ taṃ baleśvaraṃ vande । yaḥ sakalākalayor api mitātmatāyā niṣedham ādadhyāt ॥",
 "I surrender to Baleśvara, the mighty lord of dissolving the manifestation of difference, who applies rejection of the state of self-limitation for both sakala and niṣkala beings."),
(37,'प','pa','नन्दन','Nandana',
 "दुर्वृत्तजनकुसंस्कृतिसंहरणव्यावृतास्यतां दधतम् । देवममन्दं वन्दे वन्दनमानन्दनं जगताम् ॥",
 "durvṛttajanakusaṃskṛtisaṃharaṇavyāvṛtāsyatāṃ dadhatam । devam amandaṃ vande vandanam ānandanaṃ jagatām ॥",
 "I surrender to the vibrant Lord [Nandana], the bestower of bliss to the world that takes his refuge, who has fully opened his mouth to destroy the saṃskāras of those who are engaged in sinful acts."),
(38,'फ','pha','सर्वतोभद्र','Sarvatobhadra',
 "आस्थाय भैरववपुर्निजकृतेः संविभागेन । विदधातु वः स भद्रं सर्वत इह सर्वतोभद्रः ॥",
 "āsthāya bhairavavapur nijakṛteḥ saṃvibhāgena । vidadhātu vaḥ sa bhadraṃ sarvata iha sarvatobhadraḥ ॥",
 "May Sarvatobhadra grant us prosperity from all around by [he] who differentiates his creation [or maintains difference], while being situated in the [transcendent] form of Bhairava."),
(39,'ब','ba','भद्रमूर्ति','Bhadramūrti',
 "यः परमामृतकुम्भे धाम्नि परे योजयेद्गतासुमपि । जगदात्मभद्रमूर्तिर्दिशतु शिवं भद्रमूर्तिर्वः ॥",
 "yaḥ paramāmṛtakumbhe dhāmni pare yojayed gatāsum api । jagadātmabhadramūrtir diśatu śivaṃ bhadramūrtir vaḥ ॥",
 "May the auspicious image of Bhadramūrti grant us grace. [He represents] the essential nature of the world, who unites even the dead ones to the pitcher of [his] supreme nectar."),
(40,'भ','bha','शिवप्रद','Śivaprada',
 "भीममधिष्ठाय वपुर्भवमभितो भावयन्निव यः । प्रभवति हृदि भक्तिमतां शिवप्रदोऽसौ शिवोऽस्तु सताम् ॥",
 "bhīmam adhiṣṭhāya vapur bhavam abhito bhāvayann iva yaḥ । prabhavati hṛdi bhaktimatāṃ śivaprado'sau śivo'stu satām ॥",
 "May Śivaprada bestow auspiciousness to all virtuous people. While appearing as the world [of transmigration and] by assuming a fearsome form, he manifests in the hearts of those who are filled with devotion."),
(41,'म','ma','सुमनस्','Sumanas',
 "भवति यदिच्छावशतः शिवपूजा विश्वलाञ्छनं विश्वक् । विश्वं जयति स सुमनाः प्रपन्नजनमोचने सुमनाः ॥",
 "bhavati yadicchāvaśataḥ śivapūjā viśvalāñchanaṃ viśvak । viśvaṃ jayati sa sumanāḥ prapannajanamocane sumanāḥ ॥",
 "Victorious of the world is Sumanas who is pleased to liberate the beings who are suffering. It is under his will that the worship of Śiva is possible, [the worship that makes] the complete blueprint of the world possible."),
(42,'य','ya','स्पृहण','Spṛhaṇa',
 "देवं चक्रव्योमग्रन्थिगमाधारनाथमजम् । अपि परसंविद्रूढैः स्पृहणीयं स्पृहणमस्मि नतः ॥",
 "devaṃ cakravyomagranthigamādhāranātham ajam । api parasaṃvidrūḍhaiḥ spṛhaṇīyaṃ spṛhaṇam asmi nataḥ ॥",
 "I surrender to Spṛhaṇa, one desired for being established in absolute consciousness, who is birthless and is the Lord of the wheels (cakra), the empty space (vyoma), joints (granthi), channels (gama), and [sixteen] locations (ādhāra)."),
(43,'र','ra','दुर्ग','Durga',
 "समयविलोपविलुम्पनभीमवपुः सकलसम्पदां दुर्गम् । शमयतु निरर्गलं वो दुर्गमभवदुर्गतिं दुर्गः ॥",
 "samayavilopavilumpanabhīmavapuḥ sakalasaṃpadāṃ durgam । śamayatu nirargalaṃ vo durgamabhavadurgatiṃ durgaḥ ॥",
 "May Durga freely pacify the unsurpassable distress of [those] coming into being [or being in samsara], who is the fortress of all prosperities, who assumes a ferocious body in order to destroy any transgression in maintaining the order [of Bhairava's worship]."),
(44,'ल','la','भद्रकाल','Bhadrakāla',
 "भद्राणि महाकालः कलयतु वः सर्वकालमतुलगतिः । अकुलपदस्थोऽपि हि मुहुः कुलपदमभिधावतीह प्रसभम् ॥",
 "bhadrāṇi mahākālaḥ kalayatu vaḥ sarvakālam atulagatiḥ । akulapadastho'pi hi muhuḥ kulapadam abhidhāvatīha prasabham ॥",
 "May Mahākāla, [who has] incomparable speed, accomplish [all] virtues. While being seated in the state of Akula, [he] suddenly and forcefully rushes here to the state of Kula."),
(45,'व','va','मनोनुग','Manonuga',
 "सहजपरामर्शात्मकमहावीर्यसौधधौततनुम् । अभिमतसाधकसाधकमनोनुगं तं मनोनुगं नौमि ॥",
 "sahajaparāmarśātmakamahāvīryasaudhadhautatanum । abhimatasādhakasādhakamanonugaṃ taṃ manonugaṃ naumi ॥",
 "I surrender to Manonuga, the one who follows the mind of the practitioners [in] accomplishing what is desirable, the one whose body is cleansed with the nectar of great vigor of inborn recognition [of the self]."),
(46,'श','śa','कौशिक','Kauśika',
 "विद्यामायाप्रकृतित्रिप्रकृतिकमध्वसप्तकारमिदम् । विश्वत्रिशूलमभितो विकासयञ्जयति कौशिकः शम्भुः ॥",
 "vidyāmāyāprakṛtitriprakṛtikam adhvasaptakāram idam । viśvatriśūlam abhito vikāsayañ jayati kauśikaḥ śambhuḥ ॥",
 "Victorious is the benevolent Kauśika who spreads the trident of the form of the world. [He has the] threefold nature of vidyā, māyā, and prakṛti and the form of sevenfold courses [of the lotuses, wheels, and the empty voids when drawing maṇḍalas]."),
(47,'ष','ṣa','काल','Kāla',
 "शुद्धाशुद्धाध्वभिदा द्विगहरं मुद्रयत्यशेषजगत् । संविद्रूपतया यः कलयतु स किल्विषं सतां कालः ॥",
 "śuddhāśuddhādhvabhidā dvigaharaṃ mudrayaty aśeṣajagat । saṃvidrūpatayā yaḥ kalayatu sa kilviṣaṃ satāṃ kālaḥ ॥",
 "May [Lord] Kāla, who delimits the defects of the virtuous people, mark the world that is divided into twofold caves of the form of pure and impure courses [found within] the form of pure consciousness."),
(48,'स','sa','विश्वेश','Viśveśa',
 "परमानन्दसुधानिधिरुल्लसदपि बहिरशेषमिदम् । विश्रमयन्परमात्मनि विश्वेशो जयति विश्वेशः ॥",
 "paramānandasudhānidhir ullasad api bahir aśeṣam idam । viśramayan paramātmani viśveśo jayati viśveśaḥ ॥",
 "Victorious is Viśveśa, the Lord of the world, the ocean of nectar of the supreme bliss, who, even while the entirety of this [world] is manifesting outside, causes [it] to rest in the absolute self [or Bhairava consciousness]."),
(49,'ह','ha','सुशिव','Suśiva',
 "सुशिवः शिवाय भूयाद्भूयोभूयः सतां महानादः । यो वहिरुल्लसितोऽपि स्वस्माद्रूपान्न निष्क्रान्तः ॥",
 "suśivaḥ śivāya bhūyād bhūyobhūyaḥ satāṃ mahānādaḥ । yo vahir ullasito'pi svasmād rūpān na niṣkrāntaḥ ॥",
 "May Suśiva be for benevolence, who [manifests] again and again as the great sound of the virtuous ones, and even when expressed outside, He has not abandoned his essential nature."),
(50,'क्ष','kṣa','कोप','Kopa',
 "यः किल तैस्तैर्भेदैरशेषमवतार्य मातृकासारम् । शास्त्रं जगदुद्धर्ता जयति विभुः सर्ववित्कोपः ॥",
 "yaḥ kila tais tair bhedair aśeṣam avatārya mātṛkāsāram । śāstraṃ jagaduddhartā jayati vibhuḥ sarvavit kopaḥ ॥",
 "Victorious is the omnipresent and omniscient Kopa, who rescues the world by revealing the disciplines (śāstras). [He] is the essence of the phonemes [or the essence of Mātṛkās] with their respective differences."),
]
for r in R:
    add(*r, "rudras")

intro = [
"Āgamas broadly rely on phonetic structure to map the body with cosmology, speech with cosmic resonance, and all visible forms with the divine manifestations. Grounding this metaphysics on the fifty phonemes of the Sanskrit alphabet, Tantras further divide the structure into the twofold order of Mātṛkā and Mālinī, where the first stands for the order of phonemes beginning with /a/ and ending with /kṣa/, and the second order starting with the phoneme /na/ and ending with /pha/. Consistent to both these orders is the correspondence system that identifies each of the phonemes with a specific deity. When it comes to the Mātṛkā order, this is also identified as the 'constellation of words' (śabdarāśi), associated with Bhairavas. What this means is that the fifty Bhairavas correspond to the fifty Mātṛkās, or the phonemes.",
"Mālinīvijayottara, in Chapter 3, gives the list of the Bhairavas that corresponds to these phonemes but the text does not explain further, that is, it does not discuss the philosophy, iconography, or mantric correlations. Nor does it elaborate on the progressive meditative stages. Abhinavagupta addresses these lacunae by composing fifty verses, each dedicated to one of the Bhairavas. But unlike other hymns that he composed, he cryptically hid these stanzas under the guise of the benedictory or maṅgala verses. In the zenith of his philosophical thinking, in his Īśvara-Pratyabhijñā-Vivṛtti-Vivaraṇa, Abhinava composed sixteen benedictory verses, one for the beginning of each of the chapters, corresponding the Bhairavas starting with Amṛta or /a/. These sixteen Bhairavas correspond to the sixteen vowels. With regard to the Bhairavas corresponding to the consonants, he composed further benedictory verses, hidden yet again within the magnum opus, Tantrāloka, from chapter 2 onwards. These Rudras, starting with Jayarudra, therefore correspond with the consonant phonemes /ka/, continuing to the last phoneme /kṣa/.",
"These verses are not meant to be just prayers to Bhairava, or visualizations of different Bhairava forms. These in fact incorporate the entire Trika cosmology, and in a sense, present the entire philosophical system, alongside Trika soteriology, in its most essential form. Reading these fifty verses is therefore an easy way to access the totality of the practice and philosophy involved in the Trika system. — Śaivācārya Sthaneshwar Timalsina"
]

mvu = {
 "title": "Mālinīvijayottara Tantra, III.17–24b",
 "text": "अमृतोऽमृतपूर्णश्च अमृताभोऽमृतद्रवः । अमृतौघोऽमृतोर्मिश्च अमृतस्यन्दनोऽपरः ॥ ३.१७ ॥\namṛto'mṛtapūrṇaś ca amṛtābho'mṛtadravaḥ । amṛtaugho'mṛtormiś ca amṛtasyandano'paraḥ ॥\n\nअमृताङ्गोऽमृतवपुरमृतोद्गार एव च । अमृतास्योऽमृततनुस्तथा चामृतसेचनः ॥ ३.१८ ॥\namṛtāṅgo'mṛtavapuramṛtodgāra eva ca । amṛtāsyo'mṛtatanustathā cāmṛtasecanaḥ ॥\n\nतन्मूर्तिरमृतेशश्च सर्वामृतधरोऽपरः । षोडशैते समाख्याता रुद्रबीजसमुद्भवाः ॥ ३.१९ ॥\ntanmūrtiramṛteśaś ca sarvāmṛtadharo'paraḥ । ṣoḍaśaite samākhyātā rudrabījasamudbhavāḥ ॥\n\nजयश्च विजयश्चैव जयन्तश्चापराजितः । सुजयो जयरुद्रश्च जयकीर्तिर्जयावहः ॥ ३.२० ॥\njayaś ca vijayaś caiva jayantaś cāparājitaḥ । sujayo jayarudraś ca jayakīrtir jayāvahaḥ ॥\n\nजयमूर्तिर्जयोत्साहो जयदो जयवर्धनः । बलश्चातिबलश्चैव बलभद्रो बलप्रदः ॥ ३.२१ ॥\njayamūrtir jayotsāho jayado jayavardhanaḥ । balaś cātibalaś caiva balabhadro balapradaḥ ॥\n\nबलावहश्च बलवान्बलदाता बलेश्वरः । नन्दनः सर्वतोभद्रो भद्रमूर्तिः शिवप्रदः ॥ ३.२२ ॥\nbalāvahaś ca balavān baladātā baleśvaraḥ । nandanaḥ sarvatobhadro bhadramūrtiḥ śivapradaḥ ॥\n\nसुमनाः स्पृहणो दुर्गो भद्रकालो मनोनुगः । कौशिकः कालविश्वेशौ सुशिवः कोप एव च ॥ ३.२३ ॥\nsumanāḥ spṛhaṇo durgo bhadrakālo manonugaḥ । kauśikaḥ kālaviśveśau suśivaḥ kopa eva ca ॥\n\nएते योनिसमुद्भूताश्चतुस्त्रिंशत्प्रकीर्तिताः । ete yonisamudbhūtāś catustriṃśat prakīrtitāḥ ॥ ३.२४ ॥"
}

data = {
  "title": "अमृतादिस्तवः",
  "title_roman": "Amṛtādistavaḥ",
  "subtitle": "Hymn to the Fifty Bhairavas, from the Mālinīvijayottara, the Īśvara-Pratyabhijñā-Vivṛtti-Vivaraṇa, and the Tantrāloka",
  "author": "Text by Śaivācārya Abhinavagupta — Translated by Śaivācārya Sthaneshwar Timalsina",
  "intro": intro,
  "mvu": mvu,
  "entries": E
}
with open('/home/sasha/projects/sivabodha-app/data/amrta.json','w',encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=1)
print("entries:", len(E), "| vowels:", sum(1 for e in E if e['section']=='vowels'), "| rudras:", sum(1 for e in E if e['section']=='rudras'))
