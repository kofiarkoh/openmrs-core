package org.openmrs;

import java.util.*;
import java.util.stream.Collectors;

import org.openmrs.api.APIException;

/**
 * Represents patient allergies
 */
public class Allergies implements List<Allergy> {

	public static final String UNKNOWN = "Unknown";
	public static final String NO_KNOWN_ALLERGIES = "No known allergies";
	public static final String SEE_LIST = "See list";

	private String allergyStatus = UNKNOWN;
	private final List<Allergy> allergies = new ArrayList<>();

	public String getAllergyStatus() {
		return allergyStatus;
	}

	@Override
	public boolean add(Allergy allergy) {
		throwExceptionIfHasDuplicateAllergen(allergy);
		allergyStatus = SEE_LIST;
		return allergies.add(allergy);
	}

	public boolean remove(Allergy allergy) {
		boolean result = allergies.remove(allergy);
		updateAllergyStatusIfEmpty();
		return result;
	}

	@Override
	public void clear() {
		allergyStatus = UNKNOWN;
		allergies.clear();
	}

	public void confirmNoKnownAllergies() {
		if (!allergies.isEmpty()) {
			throw new APIException("Cannot confirm no known allergies if allergy list is not empty");
		}
		allergyStatus = NO_KNOWN_ALLERGIES;
	}

	@Override
	public Iterator<Allergy> iterator() {
		return allergies.iterator();
	}

	@Override
	public void add(int index, Allergy element) {
		throwExceptionIfHasDuplicateAllergen(element);
		allergies.add(index, element);
		allergyStatus = SEE_LIST;
	}

	@Override
	public boolean addAll(Collection<? extends Allergy> c) {
		throwExceptionIfHasDuplicateAllergen(c);
		allergyStatus = SEE_LIST;
		return allergies.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Allergy> c) {
		throwExceptionIfHasDuplicateAllergen(c);
		allergyStatus = SEE_LIST;
		return allergies.addAll(index, c);
	}

	@Override
	public boolean contains(Object o) {
		return allergies.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return allergies.containsAll(c);
	}

	@Override
	public Allergy get(int index) {
		return allergies.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return allergies.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return allergies.isEmpty();
	}

	@Override
	public int lastIndexOf(Object o) {
		return allergies.lastIndexOf(o);
	}

	@Override
	public ListIterator<Allergy> listIterator() {
		return allergies.listIterator();
	}

	@Override
	public ListIterator<Allergy> listIterator(int index) {
		return allergies.listIterator(index);
	}

	@Override
	public Allergy remove(int index) {
		Allergy allergy = allergies.remove(index);
		updateAllergyStatusIfEmpty();
		return allergy;
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof Allergy allergy && allergies.remove(allergy)) {
			updateAllergyStatusIfEmpty();
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = allergies.removeAll(c);
		updateAllergyStatusIfEmpty();
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = allergies.retainAll(c);
		updateAllergyStatusIfEmpty();
		return changed;
	}

	@Override
	public Allergy set(int index, Allergy element) {
		allergyStatus = SEE_LIST;
		return allergies.set(index, element);
	}

	@Override
	public int size() {
		return allergies.size();
	}

	@Override
	public List<Allergy> subList(int fromIndex, int toIndex) {
		return allergies.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return allergies.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return allergies.toArray(a);
	}

	public Allergy getAllergy(Integer allergyId) {
		return allergies.stream()
			.filter(allergy -> Objects.equals(allergy.getAllergyId(), allergyId))
			.findFirst()
			.orElse(null);
	}

	private void throwExceptionIfHasDuplicateAllergen(Allergy allergy) {
		throwExceptionIfHasAllergen(allergy, allergies);
	}

	private void throwExceptionIfHasDuplicateAllergen(Collection<? extends Allergy> inputAllergies) {
		Set<Allergy> seen = new HashSet<>();
		for (Allergy allergy : inputAllergies) {
			if (!seen.add(allergy) || containsAllergen(allergy, seen)) {
				throw new APIException("Duplicate allergens not allowed");
			}
		}
	}

	private void throwExceptionIfHasAllergen(Allergy allergy, Collection<? extends Allergy> allergyList) {
		if (containsAllergen(allergy, allergyList)) {
			throw new APIException("Duplicate allergens not allowed");
		}
	}

	public boolean containsAllergen(Allergy allergy, Collection<? extends Allergy> allergyList) {
		return allergyList.stream().anyMatch(a -> a.hasSameAllergen(allergy));
	}

	public boolean containsAllergen(Allergy allergy) {
		return containsAllergen(allergy, allergies);
	}

	private void updateAllergyStatusIfEmpty() {
		if (allergies.isEmpty()) {
			allergyStatus = UNKNOWN;
		}
	}
}
