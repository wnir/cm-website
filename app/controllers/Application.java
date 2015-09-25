package controllers;

import io.prismic.Api;
import io.prismic.Document;
import io.prismic.DocumentLinkResolver;
import io.prismic.Fragment;
import play.*;
import play.mvc.*;

import views.html.*;

import static controllers.Prismic.prismic;

public class Application extends Controller {

    final public static LinkResolver linkResolver(Api api, String ref, Http.Request request) {
        return new LinkResolver(api, ref, request);
    }

    public static class LinkResolver extends DocumentLinkResolver {
        final Api api;
        final String ref;
        final Http.Request request;

        public LinkResolver(Api api, String ref, Http.Request request) {
            this.api = api;
            this.ref = ref;
            this.request = request;
        }

        public String resolve(Fragment.DocumentLink link) {

            // For "Bookmarked" documents that use a special page
            if(isBookmark(api, link, "ctrlmed-presentation")) {
                return controllers.routes.Application.entreprise().absoluteURL(request);
            }
            /*else if(isBookmark(api, link, "jobs")) {
                return routes.Application.jobs().absoluteURL(request);
            }
            else if(isBookmark(api, link, "stores")) {
                return routes.Application.stores().absoluteURL(request);
            }

            // Store documents
            else if("store".equals(link.getType()) && !link.isBroken()) {
                return routes.Application.storeDetail(link.getId(), link.getSlug()).absoluteURL(request);
            }

            // Any product
            else if("product".equals(link.getType()) && !link.isBroken()) {
                return routes.Application.productDetail(link.getId(), link.getSlug()).absoluteURL(request);
            }

            // Job offers
            else if("job-offer".equals(link.getType()) && !link.isBroken()) {
                return routes.Application.jobDetail(link.getId(), link.getSlug()).absoluteURL(request);
            }

            // Blog
            else if("blog-post".equals(link.getType()) && !link.isBroken()) {
                return routes.Application.blogPost(link.getId(), link.getSlug()).absoluteURL(request);
            }*/

            // Any other link
            return controllers.routes.Application.brokenLink().absoluteURL(request);
        }
    }

    // -- Page not found
    public static Result pageNotFound() {
        return notFound(views.html.pageNotFound.render());
    }

    @Prismic.Action
    public static Result brokenLink() {
        return pageNotFound();
    }

    public static Result index() {
        return ok(index.render("Your new application is ready - Test."));
    }

    @Prismic.Action
    public static Result entreprise() {
        Document entreprisePage = prismic().getBookmark("ctrlmed-presentation");
        if(entreprisePage == null) {
            return pageNotFound();
        } else {
            return ok(entreprise.render(entreprisePage));
        }
    }

    public static Result produits() {
        return ok(produits.render());
    }

    public static Result events() {
        return ok(events.render());
    }

    public static Result bioExpo() {
        return ok(bioExpo.render());
    }

    public static Result siamap2015() {
        return ok(siamap.render());
    }

    public static Result partners() {
        return ok(partners.render());
    }
    @Prismic.Action
    public static Result preview(String token) {
        String indexUrl = controllers.routes.Application.index().url();
        String url = prismic().api.previewSession(token, prismic().getLinkResolver(), indexUrl);
        response().setCookie(io.prismic.Prismic.PREVIEW_COOKIE, token, 1800);
        return redirect(url);
    }

}
