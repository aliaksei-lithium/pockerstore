package io.lithium.pokerstore;

import io.lithium.pokerstore.backoffice.BackOfficeService;
import io.lithium.pokerstore.storefront.StoreFrontService;

/**
 * Defines a factory which can return Pokerstore service Object instances
 * (possibly shared or independent) when invoked.
 *
  */
public interface PokerstoreFactory {
    /**
     * Return an instance (possibly shared or indenpendent) of the BackOffice Service.
     *
     * @return an instance of the BackOffice Service
     */
    BackOfficeService createBackOfficeService();

    /**
     * Return an instance (possibly shared or indenpendent) of the StoreFront Service.
     *
     * @return an instance of the StoreFront Service
     */
    StoreFrontService createStoreFrontService();
}
