package org.teleal.cling.binding.xml;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.teleal.cling.binding.staging.MutableAction;
import org.teleal.cling.binding.staging.MutableActionArgument;
import org.teleal.cling.binding.staging.MutableAllowedValueRange;
import org.teleal.cling.binding.staging.MutableService;
import org.teleal.cling.binding.staging.MutableStateVariable;
import org.teleal.cling.binding.xml.Descriptor;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.meta.StateVariableEventDetails;
import org.teleal.cling.model.types.CustomDatatype;
import org.teleal.cling.model.types.Datatype;
import org.teleal.common.xml.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public class UDA10ServiceDescriptorBinderSAXImpl extends UDA10ServiceDescriptorBinderImpl {
	private static Logger log = Logger.getLogger(ServiceDescriptorBinder.class.getName());

	static /* synthetic */ class AnonymousClass1 {
		static final /* synthetic */ int[] $SwitchMap$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT;

		static {
			int[] iArr = new int[Descriptor.Service.ELEMENT.values().length];
			$SwitchMap$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT = iArr;
			try {
				iArr[Descriptor.Service.ELEMENT.name.ordinal()] = 1;
			} catch (NoSuchFieldError unused) {
			}
			try {
				$SwitchMap$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT[Descriptor.Service.ELEMENT.direction.ordinal()] = 2;
			} catch (NoSuchFieldError unused2) {
			}
			try {
				$SwitchMap$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT[Descriptor.Service.ELEMENT.relatedStateVariable.ordinal()] = 3;
			} catch (NoSuchFieldError unused3) {
			}
			try {
				$SwitchMap$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT[Descriptor.Service.ELEMENT.retval.ordinal()] = 4;
			} catch (NoSuchFieldError unused4) {
			}
			try {
				$SwitchMap$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT[Descriptor.Service.ELEMENT.dataType.ordinal()] = 5;
			} catch (NoSuchFieldError unused5) {
			}
			try {
				$SwitchMap$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT[Descriptor.Service.ELEMENT.defaultValue.ordinal()] = 6;
			} catch (NoSuchFieldError unused6) {
			}
			try {
				$SwitchMap$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT[Descriptor.Service.ELEMENT.allowedValue.ordinal()] = 7;
			} catch (NoSuchFieldError unused7) {
			}
		}
	}

	@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderImpl, org.teleal.cling.binding.xml.ServiceDescriptorBinder
	public <S extends Service> S describe(S s, String str) throws ValidationException, DescriptorBindingException {
		if (str == null || str.length() == 0) {
			throw new DescriptorBindingException("Null or empty descriptor");
		}
		try {
			log.fine("Reading service from XML descriptor");
			SAXParser sAXParser = new SAXParser();
			MutableService mutableService = new MutableService();
			hydrateBasic(mutableService, s);
			new RootHandler(mutableService, sAXParser);
			sAXParser.parse(new InputSource(new StringReader(str.trim())));
			return (S) mutableService.build(s.getDevice());
		} catch (ValidationException e) {
			throw e;
		} catch (Exception e2) {
			throw new DescriptorBindingException("Could not parse service descriptor: " + e2.toString(), e2);
		}
	}

	protected static class RootHandler extends ServiceDescriptorHandler<MutableService> {
		public RootHandler(MutableService mutableService, SAXParser sAXParser) {
			super(mutableService, sAXParser);
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
			if (element.equals(ActionListHandler.EL)) {
				ArrayList arrayList = new ArrayList();
				getInstance().actions = arrayList;
				new ActionListHandler(arrayList, this);
			}
			if (element.equals(StateVariableListHandler.EL)) {
				ArrayList arrayList2 = new ArrayList();
				getInstance().stateVariables = arrayList2;
				new StateVariableListHandler(arrayList2, this);
			}
		}
	}

	protected static class ActionListHandler extends ServiceDescriptorHandler<List<MutableAction>> {
		public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.actionList;

		public ActionListHandler(List<MutableAction> list, ServiceDescriptorHandler serviceDescriptorHandler) {
			super(list, serviceDescriptorHandler);
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
			if (element.equals(ActionHandler.EL)) {
				MutableAction mutableAction = new MutableAction();
				getInstance().add(mutableAction);
				new ActionHandler(mutableAction, this);
			}
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public boolean isLastElement(Descriptor.Service.ELEMENT element) {
			return element.equals(EL);
		}
	}

	protected static class ActionHandler extends ServiceDescriptorHandler<MutableAction> {
		public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.action;

		public ActionHandler(MutableAction mutableAction, ServiceDescriptorHandler serviceDescriptorHandler) {
			super(mutableAction, serviceDescriptorHandler);
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
			if (element.equals(ActionArgumentListHandler.EL)) {
				ArrayList arrayList = new ArrayList();
				getInstance().arguments = arrayList;
				new ActionArgumentListHandler(arrayList, this);
			}
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public void endElement(Descriptor.Service.ELEMENT element) throws SAXException {
			if (AnonymousClass1.$SwitchMap$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT[element.ordinal()] != 1) {
				return;
			}
			getInstance().name = getCharacters();
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public boolean isLastElement(Descriptor.Service.ELEMENT element) {
			return element.equals(EL);
		}
	}

	protected static class ActionArgumentListHandler extends ServiceDescriptorHandler<List<MutableActionArgument>> {
		public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.argumentList;

		public ActionArgumentListHandler(List<MutableActionArgument> list, ServiceDescriptorHandler serviceDescriptorHandler) {
			super(list, serviceDescriptorHandler);
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
			if (element.equals(ActionArgumentHandler.EL)) {
				MutableActionArgument mutableActionArgument = new MutableActionArgument();
				getInstance().add(mutableActionArgument);
				new ActionArgumentHandler(mutableActionArgument, this);
			}
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public boolean isLastElement(Descriptor.Service.ELEMENT element) {
			return element.equals(EL);
		}
	}

	protected static class ActionArgumentHandler extends ServiceDescriptorHandler<MutableActionArgument> {
		public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.argument;

		public ActionArgumentHandler(MutableActionArgument mutableActionArgument, ServiceDescriptorHandler serviceDescriptorHandler) {
			super(mutableActionArgument, serviceDescriptorHandler);
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public void endElement(Descriptor.Service.ELEMENT element) throws SAXException {
			int i = AnonymousClass1.$SwitchMap$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT[element.ordinal()];
			if (i == 1) {
				getInstance().name = getCharacters();
				return;
			}
			if (i == 2) {
				getInstance().direction = ActionArgument.Direction.valueOf(getCharacters().toUpperCase());
			} else if (i == 3) {
				getInstance().relatedStateVariable = getCharacters();
			} else {
				if (i != 4) {
					return;
				}
				getInstance().retval = true;
			}
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public boolean isLastElement(Descriptor.Service.ELEMENT element) {
			return element.equals(EL);
		}
	}

	protected static class StateVariableListHandler extends ServiceDescriptorHandler<List<MutableStateVariable>> {
		public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.serviceStateTable;

		public StateVariableListHandler(List<MutableStateVariable> list, ServiceDescriptorHandler serviceDescriptorHandler) {
			super(list, serviceDescriptorHandler);
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
			if (element.equals(StateVariableHandler.EL)) {
				MutableStateVariable mutableStateVariable = new MutableStateVariable();
				String value = attributes.getValue(Descriptor.Service.ATTRIBUTE.sendEvents.toString());
				mutableStateVariable.eventDetails = new StateVariableEventDetails(value != null && value.toUpperCase().equals("YES"));
				getInstance().add(mutableStateVariable);
				new StateVariableHandler(mutableStateVariable, this);
			}
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public boolean isLastElement(Descriptor.Service.ELEMENT element) {
			return element.equals(EL);
		}
	}

	protected static class StateVariableHandler extends ServiceDescriptorHandler<MutableStateVariable> {
		public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.stateVariable;

		public StateVariableHandler(MutableStateVariable mutableStateVariable, ServiceDescriptorHandler serviceDescriptorHandler) {
			super(mutableStateVariable, serviceDescriptorHandler);
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
			if (element.equals(AllowedValueListHandler.EL)) {
				ArrayList arrayList = new ArrayList();
				getInstance().allowedValues = arrayList;
				new AllowedValueListHandler(arrayList, this);
			}
			if (element.equals(AllowedValueRangeHandler.EL)) {
				MutableAllowedValueRange mutableAllowedValueRange = new MutableAllowedValueRange();
				getInstance().allowedValueRange = mutableAllowedValueRange;
				new AllowedValueRangeHandler(mutableAllowedValueRange, this);
			}
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public void endElement(Descriptor.Service.ELEMENT element) throws SAXException {
			int i = AnonymousClass1.$SwitchMap$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT[element.ordinal()];
			if (i == 1) {
				getInstance().name = getCharacters();
				return;
			}
			if (i != 5) {
				if (i != 6) {
					return;
				}
				getInstance().defaultValue = getCharacters();
			} else {
				String characters = getCharacters();
				Datatype.Builtin byDescriptorName = Datatype.Builtin.getByDescriptorName(characters);
				getInstance().dataType = byDescriptorName != null ? byDescriptorName.getDatatype() : new CustomDatatype(characters);
			}
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public boolean isLastElement(Descriptor.Service.ELEMENT element) {
			return element.equals(EL);
		}
	}

	protected static class AllowedValueListHandler extends ServiceDescriptorHandler<List<String>> {
		public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.allowedValueList;

		public AllowedValueListHandler(List<String> list, ServiceDescriptorHandler serviceDescriptorHandler) {
			super(list, serviceDescriptorHandler);
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public void endElement(Descriptor.Service.ELEMENT element) throws SAXException {
			if (AnonymousClass1.$SwitchMap$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT[element.ordinal()] != 7) {
				return;
			}
			getInstance().add(getCharacters());
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public boolean isLastElement(Descriptor.Service.ELEMENT element) {
			return element.equals(EL);
		}
	}

	protected static class AllowedValueRangeHandler extends ServiceDescriptorHandler<MutableAllowedValueRange> {
		public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.allowedValueRange;

		public AllowedValueRangeHandler(MutableAllowedValueRange mutableAllowedValueRange, ServiceDescriptorHandler serviceDescriptorHandler) {
			super(mutableAllowedValueRange, serviceDescriptorHandler);
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public void endElement(Descriptor.Service.ELEMENT element) throws SAXException {
			try {
				switch (element) {
					case minimum:
						getInstance().minimum = Long.valueOf(getCharacters());
						break;
					case maximum:
						getInstance().maximum = Long.valueOf(getCharacters());
						break;
					case step:
						getInstance().step = Long.valueOf(getCharacters());
						break;
				}
			} catch (Exception unused) {
			}
		}

		@Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
		public boolean isLastElement(Descriptor.Service.ELEMENT element) {
			return element.equals(EL);
		}
	}

	protected static class ServiceDescriptorHandler<I> extends SAXParser.Handler<I> {
		public void endElement(Descriptor.Service.ELEMENT element) throws SAXException {
		}

		public boolean isLastElement(Descriptor.Service.ELEMENT element) {
			return false;
		}

		public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
		}

		public ServiceDescriptorHandler(I i) {
			super(i);
		}

		public ServiceDescriptorHandler(I i, SAXParser sAXParser) {
			super(i, sAXParser);
		}

		public ServiceDescriptorHandler(I i, ServiceDescriptorHandler serviceDescriptorHandler) {
			super(i, serviceDescriptorHandler);
		}

		public ServiceDescriptorHandler(I i, SAXParser sAXParser, ServiceDescriptorHandler serviceDescriptorHandler) {
			super(i, sAXParser, serviceDescriptorHandler);
		}

		@Override // org.teleal.common.xml.SAXParser.Handler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
		public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
			super.startElement(str, str2, str3, attributes);
			Descriptor.Service.ELEMENT elementValueOrNullOf = Descriptor.Service.ELEMENT.valueOrNullOf(str2);
			if (elementValueOrNullOf == null) {
				return;
			}
			startElement(elementValueOrNullOf, attributes);
		}

		@Override // org.teleal.common.xml.SAXParser.Handler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
		public void endElement(String str, String str2, String str3) throws SAXException {
			super.endElement(str, str2, str3);
			Descriptor.Service.ELEMENT elementValueOrNullOf = Descriptor.Service.ELEMENT.valueOrNullOf(str2);
			if (elementValueOrNullOf == null) {
				return;
			}
			endElement(elementValueOrNullOf);
		}

		@Override // org.teleal.common.xml.SAXParser.Handler
		protected boolean isLastElement(String str, String str2, String str3) {
			Descriptor.Service.ELEMENT elementValueOrNullOf = Descriptor.Service.ELEMENT.valueOrNullOf(str2);
			return elementValueOrNullOf != null && isLastElement(elementValueOrNullOf);
		}
	}
}

